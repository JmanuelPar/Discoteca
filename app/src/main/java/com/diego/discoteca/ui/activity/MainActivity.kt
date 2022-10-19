package com.diego.discoteca.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.diego.discoteca.R
import com.diego.discoteca.data.PreferencesManager
import com.diego.discoteca.databinding.ActivityMainBinding
import com.diego.discoteca.ui.disc.DiscFragmentDirections
import com.diego.discoteca.util.*
import com.diego.discoteca.util.Constants.USER_PREFERENCES_NAME
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(USER_PREFERENCES_NAME)

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                goToScanBarcodeFragment()
            } else {
                this.showDialogTitle(
                    title = this.getString(R.string.permission_denied_title),
                    message = this.getString(R.string.permission_camera_denied_message)
                )
            }
        }

    companion object {
        private const val REQUIRED_PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    }

    private val mMainActivityViewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(PreferencesManager(this.dataStore))
    }

    lateinit var binding: ActivityMainBinding
    private lateinit var layoutNoInternet: ConstraintLayout
    private lateinit var scrimLayout: LinearLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navController: NavController
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fabButton: FloatingActionButton
    private var expandedButton = false

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNoInternetAndToolBar()
        setBottomBarNavigationAndFab()
        setInternetStatus()

        // Light / Night mode
        mMainActivityViewModel.nightMode.observe(this) { nightMode ->
            AppCompatDelegate.setDefaultNightMode(nightMode)
            delegate.applyDayNight()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_light_dark_menu, menu)
        setItemNightMode(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.light_mode -> {
                item.isChecked = !item.isChecked
                mMainActivityViewModel.onNightModeSelected(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            R.id.dark_mode -> {
                item.isChecked = !item.isChecked
                mMainActivityViewModel.onNightModeSelected(AppCompatDelegate.MODE_NIGHT_YES)
                true
            }
            R.id.system_default -> {
                item.isChecked = !item.isChecked
                mMainActivityViewModel.onNightModeSelected(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (expandedButton) collapseButton()
        else super.onBackPressed()
    }

    private fun setItemNightMode(menu: Menu) {
        lifecycleScope.launch {
            when (mMainActivityViewModel.nightModeFlow.first()) {
                AppCompatDelegate.MODE_NIGHT_NO -> menu.findItem(R.id.light_mode).isChecked = true
                AppCompatDelegate.MODE_NIGHT_YES -> menu.findItem(R.id.dark_mode).isChecked = true
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> menu.findItem(R.id.system_default).isChecked =
                    true
            }
        }
    }

    private fun setNoInternetAndToolBar() {
        layoutNoInternet = binding.layoutNoInternet
        scrimLayout = binding.layoutScrim
        fabButton = binding.fab
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setBottomBarNavigationAndFab() {
        bottomAppBar = binding.bottomAppBar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = binding.bottomNavigation.apply {
            background = null
            menu.getItem(3).isEnabled = false
            setupWithNavController(navController)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.discFragment, R.id.infoFragment, R.id.interFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.discFragment -> {
                    mTransitionFadeThroughExit()
                    showScrimLayout(false)
                    setBottomBarFab(true)
                }
                R.id.infoFragment -> {
                    mTransitionFadeThroughExit()
                    showScrimLayout(false)
                    setBottomBarFab(false)
                }
                R.id.interFragment -> {
                    mTransitionFadeThroughExit()
                    showScrimLayout(false)
                    setBottomBarFab(false)
                }
                else -> hideBottomBarHideFabHandle()
            }
        }

        fabButton.setOnClickListener { expandButton() }

        scrimLayout.setOnClickListener {
            // We do nothing } }
        }
    }

    private fun setInternetStatus() {
        ConnectivityStatus(this).observe(this) { isConnected ->
            if (isConnected) hideNoInternet() else showNoInternet()
        }
    }

    private fun hideToolBar() {
        mTransitionDelayed(
            sceneRoot = toolbar,
            mDuration = 84L
        )
        supportActionBar?.hide()
    }

    private fun showToolBar() {
        mTransitionDelayed(
            sceneRoot = toolbar,
            mDuration = 150L
        )
        supportActionBar?.show()
    }

    private fun hideNoInternet() {
        mTransitionDelayed(
            sceneRoot = layoutNoInternet,
            mDuration = 84L
        )
        layoutNoInternet.visibility = View.GONE
    }

    private fun showNoInternet() {
        mTransitionDelayed(
            sceneRoot = layoutNoInternet,
            mDuration = 150L
        )
        layoutNoInternet.visibility = View.VISIBLE
    }

    fun setBottomBarFab(isShow: Boolean) {
        showBottomBar()
        if (isShow) fabButton.show() else fabButton.hide()
    }

    private fun hideBottomBarHideFabHandle() {
        fabButton.hide()
        hideBottomBarHandle()
    }

    fun hideBottomBarHideFab() {
        hideBottomBar()
        fabButton.hide()
    }

    private fun showBottomBar() {
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.performShow()
    }

    private fun hideBottomBar() {
        bottomAppBar.visibility = View.GONE
        bottomAppBar.performHide()
    }

    private fun hideBottomBarHandle() {
        bottomAppBar.run {
            performHide()
            animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    bottomAppBar.visibility = View.GONE
                    fabButton.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    private fun expandButton() {
        hideToolBar()
        hideBottomBar()
        fabButton.visibility = View.INVISIBLE

        binding.run {
            scanItem.setOnClickListener {
                collapseButton()
                checkAndGoToScanDisc()
            }

            manuallyItem.setOnClickListener {
                collapseButton()
                goToAddDiscManually()
            }

            menuCardScrim.visibility = View.VISIBLE
            menuCardScrim.setOnClickListener { collapseButton() }
        }

        expandedButton = true
        mTransitionDelayed(
            sceneRoot = binding.menuCardView,
            mDuration = 150L
        )
        binding.menuCardView.visibility = View.VISIBLE
    }

    private fun collapseButton() {
        showToolBar()
        showBottomBar()
        binding.menuCardScrim.visibility = View.GONE
        expandedButton = false
        mTransitionDelayed(
            sceneRoot = binding.menuCardView,
            mDuration = 54L
        )
        binding.menuCardView.visibility = View.INVISIBLE
        fabButton.show()
    }

    private fun checkAndGoToScanDisc() {
        checkPermissionCamera()
    }

    private fun checkPermissionCamera() {
        when {
            ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION_CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> goToScanBarcodeFragment()
            shouldShowRequestPermissionRationale(REQUIRED_PERMISSION_CAMERA) -> showAlertDialogRationale()
            else -> requestPermissionLauncher.launch(REQUIRED_PERMISSION_CAMERA)
        }
    }

    private fun showAlertDialogRationale() {
        this.showDialogMessageOneButton(
            message = this.getString(R.string.permission_camera_rationale_body),
            positiveButton = this.getString(R.string.ok)
        ) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION_CAMERA)
        }
    }

    private fun goToScanBarcodeFragment() {
        navigate(
            DiscFragmentDirections.actionDiscFragmentToScanBarcodeFragment(Destination.API)
        )
    }

    private fun goToAddDiscManually() {
        navigate(DiscFragmentDirections.actionDiscFragmentToAddDiscFragment())
    }

    fun navigate(directions: NavDirections) {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }

            reenterTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }

            navController.navigate(directions)
        }
    }

    fun navigateTo(directions: NavDirections) {
        currentNavigationFragment?.apply {
            navController.navigate(directions)
        }
    }

    fun navigateToWithExtras(directions: NavDirections, extras: FragmentNavigator.Extras) {
        currentNavigationFragment?.apply {
            navController.navigate(directions, extras)
        }
    }

    fun navigatePopStack() {
        currentNavigationFragment?.apply {
            navController.popBackStack()
        }
    }

    private fun mTransitionDelayed(sceneRoot: ViewGroup, mDuration: Long) {
        delayedTransition(
            sceneRoot = sceneRoot,
            transition = MaterialFade().apply { duration = mDuration }
        )
    }

    private fun mTransitionFadeThroughExit() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }
    }

    fun getBottomAppBar(): View {
        return bottomAppBar
    }

    fun getFabView(): View {
        return fabButton
    }

    fun showSnackBar(uiText: UIText, anchorView: View) {
        lifecycleScope.launch {
            showBottomBar()
            delay(300)
            binding.root.showSnackBar(
                message = getMyUIText(uiText),
                anchorView = anchorView
            )
        }
    }

    fun showSnackBarNoAnchor(uiText: UIText) {
        binding.root.showSnackBarNoAnchor(this.getMyUIText(uiText))
    }

    fun showScrimLayout(display: Boolean) {
        scrimLayout.visibility = if (display) View.VISIBLE else View.GONE
    }
}