package com.diego.discoteca.ui.updateDisc

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentUpdateDiscBinding
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.diego.discoteca.util.showBottomSheetModal
import com.diego.discoteca.util.showDialogTitle
import com.google.android.material.transition.MaterialSharedAxis

class UpdateDiscFragment : Fragment(R.layout.fragment_update_disc), MenuProvider {

    private val args: UpdateDiscFragmentArgs by navArgs()
    private val mUpdateDiscViewModel: UpdateDiscViewModel by viewModels {
        UpdateDiscViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discId = args.discId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUpdateDiscBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            updateDiscViewModel = mUpdateDiscViewModel
            itDiscArtist.setEndIconOnClickListener {
                showDialogTitle(
                    title = getString(R.string.artist_group_name),
                    message = getString(R.string.information_artist_message)
                )
            }
            itDiscYear.setEndIconOnClickListener {
                showDialogTitle(
                    title = getString(R.string.disc_year_release),
                    message = getString(R.string.information_year_message)
                )
            }
        }

        // Go to DiscFragment
        mUpdateDiscViewModel.navigateToDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                goToDiscFragment(
                    UpdateDiscFragmentDirections.actionUpdateDiscFragmentToDiscFragment(
                        uiText = UIText.DiscUpdated,
                        idAdded = id
                    )
                )
                mUpdateDiscViewModel.onNavigateToDiscDone()
            }
        }

        // Show BottomSheet
        mUpdateDiscViewModel.showBottomSheet.observe(viewLifecycleOwner) {
            it?.let { discUpdate ->
                childFragmentManager.showBottomSheetModal(
                    artistName = discUpdate.name,
                    title = discUpdate.title,
                    year = discUpdate.year
                ) {
                    mUpdateDiscViewModel.updateDisc(discUpdate)
                }
                mUpdateDiscViewModel.showBottomSheetDone()
            }
        }

        setupMenu()
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

    private fun showDialogTitle(title: String, message: String) {
        requireContext().showDialogTitle(
            title = title,
            message = message
        )
    }

    private fun goToDiscFragment(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}