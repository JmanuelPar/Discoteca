package com.diego.discoteca.ui.interaction

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentInteractionBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.*
import com.diego.discoteca.util.Constants.DATABASE_NAME
import com.diego.discoteca.util.Constants.G_DRIVE_FOLDER_NAME
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.transition.MaterialFadeThrough
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.UnknownHostException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * https://developers.google.com/identity/sign-in/android/start
 * https://developers.google.com/drive/api/v3/about-sdk
 * https://developers.google.com/drive/api/v3/quickstart/java
 * https://developers.google.com/drive/api/v3/reference
 */
class InteractionFragment : Fragment(R.layout.fragment_interaction), CoroutineScope by MainScope(),
    MenuProvider {

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
    private val requestSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    private lateinit var binding: FragmentInteractionBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val mInteractionViewModel: InteractionViewModel by viewModels {
        InteractionViewModelFactory((requireContext().applicationContext as DiscotecaApplication).discsRepository)
    }

    private val callbackOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            // We do nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callbackOnBackPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInteractionBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            interactionViewModel = mInteractionViewModel
        }

        /* Configure sign-in to request the user's ID, email address, and basic
           profile. ID and basic profile are included in DEFAULT_SIGN_IN */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_FILE))
            .requestEmail()
            .requestProfile()
            .build()

        /* Build a GoogleSignInClient with access to the Google Sign-In API and the
           options specified by gso */
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Google Sign-In button
        binding.signInButton.setOnClickListener {
            if (ConnectivityStatus.isInternetAvailable) signIn()
            else showDialogTitleAction(
                getString(R.string.no_connect_message)
            ) {
                goToDiscFragment(UIText.NoDisplay)
            }
        }

        mInteractionViewModel.driveLogOutClicked.observe(viewLifecycleOwner) {
            if (it == true) {
                signOut()
                mInteractionViewModel.driveLogOutClickedDone()
            }
        }

        mInteractionViewModel.driveDisconnectClicked.observe(viewLifecycleOwner) {
            if (it == true) {
                revokeAccess()
                mInteractionViewModel.driveDisconnectClickedDone()
            }
        }

        mInteractionViewModel.iconGDriveUploadClicked.observe(viewLifecycleOwner) {
            if (it == true) {
                showDialogMessageAction(
                    message = getString(R.string.answer_back_up),
                    positiveButton = getString(R.string.back_up)
                ) {
                    uploadDatabaseToGDrive()
                }
                mInteractionViewModel.iconGDriveUploadClickedDone()
            }
        }

        mInteractionViewModel.iconGDriveDownloadClicked.observe(viewLifecycleOwner) {
            if (it == true) {
                showDialogMessageAction(
                    message = getString(R.string.answer_restore),
                    positiveButton = getString(R.string.restore)
                ) {
                    downloadDatabaseFromGDrive()
                }
                mInteractionViewModel.iconGDriveDownloadClickedDone()
            }
        }

        setupMenu()
    }

    override fun onStart() {
        super.onStart()
        // Check if the user is already signed in and all required scopes are granted
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        when {
            account != null && GoogleSignIn.hasPermissions(
                account,
                Scope(Scopes.DRIVE_FILE)
            ) -> updateUI(account)
            else -> updateUI(null)
        }
        setUploadDownloadTime()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    private fun setupMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Signed in successfully, show authenticated UI
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Timber.e("Exception on handleSignInResult : $e")
            // Signed out, show unauthenticated UI
            updateUI(null)
        }
        setUploadDownloadTime()
    }

    private fun signIn() {
        val signIntent = mGoogleSignInClient.signInIntent
        try {
            requestSignInLauncher.launch(signIntent)
        } catch (exception: Exception) {
            Timber.e("Exception on signIn : $exception")
            showDialogTitleAction(
                getString(R.string.sign_in_unexpected_error)
            ) {
                goToDiscFragment(UIText.NoDisplay)
            }
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            updateUI(null)
        }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(requireActivity()) {
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        mInteractionViewModel.updateUI(account)
    }

    private fun getDriveService(): Drive? {
        GoogleSignIn.getLastSignedInAccount(requireContext())?.let { googleAccount ->
            val credential = GoogleAccountCredential.usingOAuth2(
                requireContext(), listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = googleAccount.account!!

            return Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(getString(R.string.app_name))
                .build()
        }
        return null
    }

    private fun uploadDatabaseToGDrive() {
        getDriveService()?.let { googleDriveService ->
            updateScrimLayoutAndOnBackPressed(
                display = true,
                enabled = true
            )
            updateProgressLinearAndIconGDriveUpload(
                visibility = true,
                enabled = false
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Get id of folder MyDiscotecaDatabase in G Drive, Upload database ok
                    val folderId = createAndGetFolderId(googleDriveService)

                    startGDriveFileBackUpAndUpload(
                        googleDriveService = googleDriveService,
                        folderId = folderId,
                        fileDatabase = File(
                            requireContext().getDatabasePath(
                                DATABASE_NAME
                            ).absolutePath
                        )
                    )

                    withContext(Dispatchers.Main) {
                        updateProgressLinearAndIconGDriveUpload(
                            visibility = false,
                            enabled = true
                        )
                        mInteractionViewModel.updateIconGDriveDownload(true)
                        showSnackBar(UIText.DatabaseBackUp)
                        updateScrimLayoutAndOnBackPressed(
                            display = false,
                            enabled = false
                        )
                    }
                } catch (exception: Exception) {
                    Timber.e("Exception on uploadDatabaseToGDrive : $exception")
                    withContext(Dispatchers.Main) {
                        updateProgressLinearAndIconGDriveUpload(
                            visibility = false,
                            enabled = true
                        )
                        mInteractionViewModel.updateIconGDriveDownload(true)
                        updateScrimLayoutAndOnBackPressed(
                            display = false,
                            enabled = false
                        )

                        when (exception) {
                            is UnknownHostException -> {
                                mInteractionViewModel.updateIconGDriveUpload(false)
                                mInteractionViewModel.updateIconGDriveDownload(false)
                                showDialogTitleAction(
                                    getString(R.string.no_connect_message)
                                ) {
                                    goToDiscFragment(UIText.NoDisplay)
                                }
                            }
                            is UserRecoverableAuthIOException -> showDialogTitleAction(
                                getString(R.string.google_drive_authorization_message)
                            ) {
                                signIn()
                            }
                            else -> showDialogTitle(getString(R.string.error_message))
                        }
                    }
                }
            }
        }
    }

    private fun createAndGetFolderId(googleDriveService: Drive): String {
        // Check folder MyDatabaseDiscotecaApp in G Drive
        val resultFolder = getGDriveFolder(googleDriveService)

        return when {
            resultFolder.files.size > 0 -> {
                // Folder present, already created in G Drive
                resultFolder.files[0].id
            }
            else -> {
                // Create a folder MyDatabaseDiscotecaApp in G Drive
                val fileMetadata = com.google.api.services.drive.model.File().apply {
                    name = G_DRIVE_FOLDER_NAME
                    mimeType = "application/vnd.google-apps.folder"
                }

                val file = googleDriveService.files()
                    .create(fileMetadata)
                    .setFields("id")
                    .execute()
                file.id
            }
        }
    }

    private fun startGDriveFileBackUpAndUpload(
        googleDriveService: Drive,
        folderId: String,
        fileDatabase: File
    ) {
        // Get the number of discs present in database currently
        val numberDiscsDatabase = binding.discsInteractionMessage.text.toString()

        val resultFileBackUp = getGDriveFileBackUp(
            googleDriveService = googleDriveService,
            folderId = folderId
        )

        val resultFileRestoration = getGDriveFileRestoration(
            googleDriveService = googleDriveService,
            folderId = folderId
        )

        val fileMetadata = com.google.api.services.drive.model.File()
        val fileContent = FileContent("application/octet-stream", fileDatabase)

        // Get the back up database time in folder G Drive
        val backUpTime = when {
            resultFileBackUp.files.size > 0 -> {
                /* File back up database is already created in folder G Drive
                   We update this in G Drive */
                val fileId = resultFileBackUp.files[0].id
                fileMetadata.name = "${fileDatabase.name}-$numberDiscsDatabase" + ".db"

                googleDriveService.files()
                    .update(fileId, fileMetadata, fileContent)
                    .setFields("modifiedTime")
                    .execute().modifiedTime.toString()
            }
            else -> {
                /* File back up database is not created
                   Create file back up / upload database in G Drive */
                fileMetadata.apply {
                    name = "${fileDatabase.name}-$numberDiscsDatabase" + ".db"
                    parents = Collections.singletonList(folderId)
                }

                googleDriveService.Files().create(fileMetadata, fileContent)
                    .setFields("createdTime")
                    .execute().createdTime.toString()
            }
        }

        val backUpTimeFormat =
            "${getMyInteraction(Interaction.Time(backUpTime))}\n$numberDiscsDatabase"

        val restorationTimeFormat = getRestorationTimeFormat(
            resultFileRestoration = resultFileRestoration,
            interaction = Interaction.ExistsBackUp
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            updateBackUpTime(backUpTimeFormat)
            updateRestorationTime(restorationTimeFormat)
        }
    }

    private fun downloadDatabaseFromGDrive() {
        getDriveService()?.let { googleDriveService ->
            updateScrimLayoutAndOnBackPressed(
                display = true,
                enabled = true
            )

            updateProgressLinearAndIconGDriveDownload(
                visibility = true,
                enabled = false
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Check folder MyDiscotecaDatabase in G Drive
                    val resultFolder = getGDriveFolder(googleDriveService)
                    when {
                        resultFolder.files.size > 0 -> {
                            // Folder present in G Drive
                            val folderId = resultFolder.files[0].id

                            // Check file back up database in G Drive
                            val resultFileBackUp = getGDriveFileBackUp(
                                googleDriveService = googleDriveService,
                                folderId = folderId
                            )

                            when {
                                // File back up database present in G Drive
                                resultFileBackUp.files.size > 0 -> {
                                    // Path of my database
                                    val fileDatabasePath = File(
                                        requireContext().getDatabasePath(DATABASE_NAME).absolutePath
                                    )
                                    val fileBackUpId = resultFileBackUp.files[0].id

                                    // Restore database from G Drive
                                    googleDriveService
                                        .files()
                                        .get(fileBackUpId)
                                        .executeMediaAndDownloadTo(
                                            FileOutputStream(
                                                fileDatabasePath
                                            )
                                        )

                                    // File restoration in G Drive
                                    startGDriveFileRestorationAndCreate(
                                        googleDriveService = googleDriveService,
                                        folderId = folderId
                                    )

                                    withContext(Dispatchers.Main) {
                                        updateProgressLinearAndIconGDriveDownload(
                                            visibility = false,
                                            enabled = true
                                        )

                                        updateScrimLayoutAndOnBackPressed(
                                            display = false,
                                            enabled = false
                                        )

                                        goToDiscFragment(UIText.DatabaseRestored)
                                    }
                                }
                                else -> {
                                    /* Folder present in G Drive :
                                       Error : file back up database not present in G Drive, unable to restore
                                       Get restoration time */
                                    val backUpTimeFormat =
                                        getMyInteraction(Interaction.NotExistsBackUpError)

                                    // Check file restoration in folder G Drive
                                    val resultFileRestoration = getGDriveFileRestoration(
                                        googleDriveService = googleDriveService,
                                        folderId = folderId
                                    )

                                    val restorationTimeFormat = getRestorationTimeFormat(
                                        resultFileRestoration = resultFileRestoration,
                                        interaction = Interaction.NotExistsRestorationError
                                    )

                                    withContext(Dispatchers.Main) {
                                        updateProgressLinearAndIconGDriveDownload(
                                            visibility = false,
                                            enabled = false
                                        )
                                        updateBackUpTime(backUpTimeFormat)
                                        updateRestorationTime(restorationTimeFormat)
                                        showSnackBar(UIText.DatabaseNotRestored)
                                        updateScrimLayoutAndOnBackPressed(
                                            display = false,
                                            enabled = false
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            /* Folder not present in G Drive :
                               File back up database not present, unable to restore
                               File restoration not present */
                            val backUpTimeFormat =
                                getMyInteraction(Interaction.NotExistsBackUp)

                            val restorationTimeFormat =
                                getMyInteraction(Interaction.NotExistsRestoration)

                            withContext(Dispatchers.Main) {
                                updateProgressLinearAndIconGDriveDownload(
                                    visibility = false,
                                    enabled = false
                                )
                                updateBackUpTime(backUpTimeFormat)
                                updateRestorationTime(restorationTimeFormat)
                                showSnackBar(UIText.DatabaseNotRestored)
                                updateScrimLayoutAndOnBackPressed(
                                    display = false,
                                    enabled = false
                                )
                            }
                        }
                    }
                } catch (exception: Exception) {
                    Timber.e("Exception on downloadDatabaseFromGDrive : $exception")
                    withContext(Dispatchers.Main) {
                        updateProgressLinearAndIconGDriveDownload(
                            visibility = false,
                            enabled = true
                        )

                        updateScrimLayoutAndOnBackPressed(
                            display = false,
                            enabled = false
                        )

                        when (exception) {
                            is UnknownHostException -> {
                                mInteractionViewModel.updateIconGDriveUpload(false)
                                mInteractionViewModel.updateIconGDriveDownload(false)
                                showDialogTitleAction(
                                    getString(R.string.no_connect_message)
                                ) {
                                    goToDiscFragment(UIText.NoDisplay)
                                }
                            }
                            is UserRecoverableAuthIOException -> showDialogTitleAction(
                                getString(R.string.google_drive_authorization_message)
                            ) {
                                signIn()
                            }
                            else -> showDialogTitle(getString(R.string.error_message))
                        }
                    }
                }
            }
        }
    }

    private fun startGDriveFileRestorationAndCreate(
        googleDriveService: Drive,
        folderId: String
    ) {
        // File back up database present in G Drive
        val resultFileBackUp = getGDriveFileBackUp(
            googleDriveService = googleDriveService,
            folderId = folderId
        )

        val resultFileRestoration = getGDriveFileRestoration(
            googleDriveService = googleDriveService,
            folderId = folderId
        )

        // Get the number of discs of the last back up in G Drive
        val numberDiscsLastBackUp = getMyInteraction(
            Interaction.BackUp(resultFileBackUp.files[0].name)
        )

        val fileMetadata = com.google.api.services.drive.model.File()

        val restorationTime = when {
            resultFileRestoration.files.size > 0 -> {
                /* File restoration is already created in folder G Drive
                   We update this in G Drive */
                val fileId = resultFileRestoration.files[0].id
                val fileName = "time_restore-$numberDiscsLastBackUp"
                fileMetadata.name = fileName

                googleDriveService.files()
                    .update(fileId, fileMetadata)
                    .setFields("modifiedTime")
                    .execute().modifiedTime.toString()
            }
            else -> {
                /* File restoration is not created
                   Create file restoration / upload in G Drive */
                fileMetadata.apply {
                    name = "time_restore-$numberDiscsLastBackUp"
                    mimeType = "text/plain"
                    parents = Collections.singletonList(folderId)
                }

                googleDriveService.files().create(fileMetadata)
                    .setFields("createdTime")
                    .execute().createdTime.toString()
            }
        }

        val restorationTimeFormat =
            "${getMyInteraction(Interaction.Time(restorationTime))}\n$numberDiscsLastBackUp\n\n${
                getMyInteraction(Interaction.ExistsBackUp)
            }"

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            updateRestorationTime(restorationTimeFormat)
        }
    }

    private fun setUploadDownloadTime() {
        getDriveService()?.let { googleDriveService ->
            updateScrimLayoutAndOnBackPressed(
                display = true,
                enabled = true
            )

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Check MyDatabaseDiscotecaApp in G Drive
                    val resultFolder = getGDriveFolder(googleDriveService)
                    when {
                        resultFolder.files.size > 0 -> {
                            // Get id of folder present in G Drive
                            val folderId = resultFolder.files[0].id

                            // Check file back up database in folder G Drive
                            val resultFileBackUp = getGDriveFileBackUp(
                                googleDriveService = googleDriveService,
                                folderId = folderId
                            )

                            // Check file restoration in folder G Drive
                            val resultFileRestoration = getGDriveFileRestoration(
                                googleDriveService = googleDriveService,
                                folderId = folderId
                            )

                            when {
                                resultFileBackUp.files.size > 0 -> {
                                    /* Folder present in G Drive
                                       File back up database present in folder G Drive
                                       Get the number of the discs of the last back up
                                       Get last back up time
                                       Get restoration time */
                                    val numberDiscsLastBackUp = getMyInteraction(
                                        Interaction.BackUp(resultFileBackUp.files[0].name)
                                    )

                                    val backUpTime =
                                        resultFileBackUp.files[0].modifiedTime.toString()

                                    val backUpTimeFormat =
                                        "${getMyInteraction(Interaction.Time(backUpTime))}\n$numberDiscsLastBackUp"

                                    val restorationTimeFormat = getRestorationTimeFormat(
                                        resultFileRestoration = resultFileRestoration,
                                        interaction = Interaction.ExistsBackUp
                                    )

                                    withContext(Dispatchers.Main) {
                                        mInteractionViewModel.updateIconGDriveDownload(true)
                                        updateBackUpTime(backUpTimeFormat)
                                        updateRestorationTime(restorationTimeFormat)
                                        updateScrimLayoutAndOnBackPressed(
                                            display = false,
                                            enabled = false
                                        )
                                    }
                                }
                                else -> {
                                    /* Folder present in G Drive
                                       Error : file back up database not present in G Drive, unable to restore
                                       No number of the discs of the last back up
                                       No last back up time
                                       Get restoration time */
                                    val backUpTimeFormat =
                                        getMyInteraction(Interaction.NotExistsBackUpError)

                                    val restorationTimeFormat = getRestorationTimeFormat(
                                        resultFileRestoration = resultFileRestoration,
                                        interaction = Interaction.NotExistsRestorationError
                                    )

                                    withContext(Dispatchers.Main) {
                                        mInteractionViewModel.updateIconGDriveDownload(false)
                                        updateBackUpTime(backUpTimeFormat)
                                        updateRestorationTime(restorationTimeFormat)
                                        updateScrimLayoutAndOnBackPressed(
                                            display = false,
                                            enabled = false
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            /* Folder not present in G Drive :
                               File back up database not present
                               File restoration not present */
                            val backUpTimeFormat =
                                getMyInteraction(Interaction.NotExistsBackUp)

                            val restorationTimeFormat =
                                getMyInteraction(Interaction.NotExistsRestoration)

                            withContext(Dispatchers.Main) {
                                updateBackUpTime(backUpTimeFormat)
                                updateRestorationTime(restorationTimeFormat)
                                updateScrimLayoutAndOnBackPressed(
                                    display = false,
                                    enabled = false
                                )
                            }
                        }
                    }
                } catch (exception: Exception) {
                    Timber.e("Exception on setUploadDownloadTime : $exception")
                    withContext(Dispatchers.Main) {
                        updateScrimLayoutAndOnBackPressed(
                            display = false,
                            enabled = false
                        )

                        when (exception) {
                            is UnknownHostException -> {
                                mInteractionViewModel.updateIconGDriveUpload(false)
                                mInteractionViewModel.updateIconGDriveDownload(false)
                                showDialogTitleAction(
                                    getString(R.string.no_connect_message)
                                ) {
                                    goToDiscFragment(UIText.NoDisplay)
                                }
                            }
                            is UserRecoverableAuthIOException -> showDialogTitleAction(
                                getString(R.string.google_drive_authorization_message)
                            ) {
                                signIn()
                            }
                            else -> showDialogTitle(getString(R.string.error_message))
                        }
                    }
                }
            }
        }
    }

    private fun getGDriveFolder(googleDriveService: Drive) =
        googleDriveService.files()
            .list().apply {
                q =
                    "mimeType='application/vnd.google-apps.folder' and name='$G_DRIVE_FOLDER_NAME' and trashed=false"
                spaces = "drive"
            }.execute()

    private fun getGDriveFileBackUp(googleDriveService: Drive, folderId: String) =
        googleDriveService.files()
            .list().apply {
                q =
                    "mimeType='application/octet-stream' and '$folderId' in parents and trashed=false"
                spaces = "drive"
                fields = "files(id,name,createdTime,modifiedTime)"
            }.execute()

    private fun getGDriveFileRestoration(googleDriveService: Drive, folderId: String) =
        googleDriveService.files()
            .list().apply {
                q =
                    "mimeType='text/plain' and '$folderId' in parents and trashed=false"
                spaces = "drive"
                fields = "files(id,name,createdTime,modifiedTime)"
            }.execute()

    private fun getMyInteraction(interaction: Interaction): String {
        return when (interaction) {
            is Interaction.ExistsBackUp -> getMyString(R.string.back_up_present_in_drive)
            is Interaction.NotExistsBackUp -> getMyString(R.string.no_back_up_in_drive)
            is Interaction.NotExistsBackUpError -> getMyString(R.string.error_no_back_up_in_drive)
            is Interaction.NotExistsRestoration -> getMyString(R.string.no_restore_done)
            is Interaction.NotExistsRestorationError -> getMyString(R.string.error_no_back_up_in_drive_unable_to_restore)
            is Interaction.BackUp -> {
                //Example fileName : disc_database-1 disque.db
                interaction.fileName.split("-")[1].split(".")[0]
            }
            is Interaction.Restoration -> {
                //Example fileName : time_restore-1 disque
                interaction.fileName.split("-")[1]
            }
            is Interaction.Time -> {
                try {
                    val zonedDateTime =
                        Instant.parse(interaction.time).atZone(ZoneId.systemDefault())
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss")
                    dateTimeFormatter.format(zonedDateTime)
                } catch (exception: Exception) {
                    getString(R.string.not_specified)
                }
            }
        }
    }

    private fun getMyString(stringId: Int): String {
        return if (isAdded && context != null) getString(stringId) else ""
    }

    private fun getRestorationTimeFormat(
        resultFileRestoration: FileList,
        interaction: Interaction
    ): String {
        return when {
            resultFileRestoration.files.size > 0 -> {
                /* File restoration present in folder MyDatabaseDiscotecaApp G Drive
                   Get the number of the discs of the last restoration
                   Get last restoration time */
                val numberDiscsLastRestoration = getMyInteraction(
                    Interaction.Restoration(resultFileRestoration.files[0].name)
                )

                val restorationTime = resultFileRestoration.files[0].modifiedTime.toString()

                "${getMyInteraction(Interaction.Time(restorationTime))}\n$numberDiscsLastRestoration\n\n${
                    getMyInteraction(interaction)
                }"
            }
            else -> {
                /* File restoration not present in folder MyDatabaseDiscotecaApp G Drive
                   No number of the discs of the last restoration
                   No last restoration time */
                "${getMyInteraction(Interaction.NotExistsRestoration)}\n\n${
                    getMyInteraction(interaction)
                }"
            }
        }
    }

    private fun updateBackUpTime(time: String) {
        mInteractionViewModel.updateUploadTime(time)
    }

    private fun updateRestorationTime(time: String) {
        mInteractionViewModel.updateDownloadTime(time)
    }

    private fun updateProgressLinearAndIconGDriveUpload(visibility: Boolean, enabled: Boolean) {
        mInteractionViewModel.updateProgressLinearGDriveUpload(visibility)
        mInteractionViewModel.updateIconGDriveUpload(enabled)
    }

    private fun updateProgressLinearAndIconGDriveDownload(visibility: Boolean, enabled: Boolean) {
        mInteractionViewModel.updateProgressLinearGDriveDownload(visibility)
        mInteractionViewModel.updateIconGDriveDownload(enabled)
    }

    private fun updateScrimLayoutAndOnBackPressed(display: Boolean, enabled: Boolean) {
        if (activity is MainActivity) {
            (activity as MainActivity).showScrimLayout(display)
        }
        callbackOnBackPressed.isEnabled = enabled
    }

    private fun showSnackBar(uiText: UIText) {
        if (activity is MainActivity) {
            (activity as MainActivity).showSnackBar(
                uiText = uiText,
                anchorView = (activity as MainActivity).getBottomAppBar()
            )
        }
    }

    private fun showDialogTitle(message: String) {
        requireContext().showDialogTitle(
            title = getString(R.string.app_name),
            message = message
        )
    }

    private fun showDialogTitleAction(message: String, action: () -> Unit) {
        requireContext().showDialogTitleAction(
            title = getString(R.string.app_name),
            message = message
        ) {
            action()
        }
    }

    private fun showDialogMessageAction(
        message: String,
        positiveButton: String,
        action: () -> Unit
    ) {
        requireContext().showDialogMessageAction(
            message,
            positiveButton
        ) {
            action()
        }
    }

    private fun goToDiscFragment(uiText: UIText) {
        findNavController().navigate(
            InteractionFragmentDirections.actionInterFragmentToDiscFragment(
                uiText = uiText,
                idAdded = -1L
            )
        )
    }
}

sealed class Interaction {
    object ExistsBackUp : Interaction()
    object NotExistsBackUp : Interaction()
    object NotExistsBackUpError : Interaction()
    object NotExistsRestoration : Interaction()
    object NotExistsRestorationError : Interaction()
    data class BackUp(val fileName: String) : Interaction()
    data class Restoration(val fileName: String) : Interaction()
    data class Time(val time: String) : Interaction()
}
