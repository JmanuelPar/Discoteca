package com.diego.discoteca.ui.discPresentDetail

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.databinding.FragmentDiscPresentDetailBinding
import com.diego.discoteca.util.materialSharedAxisExitReenterTransition
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class DiscPresentDetailFragment : Fragment(R.layout.fragment_disc_present_detail), MenuProvider {

    private val args: DiscPresentDetailFragmentArgs by navArgs()
    private val mDiscPresentDetailViewModel: DiscPresentDetailViewModel by viewModels {
        DiscPresentDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discPresent = args.discPresent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.my_nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDiscPresentDetailBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discPresentDetailViewModel = mDiscPresentDetailViewModel
        }

        // PopStack
        mDiscPresentDetailViewModel.navigatePopStack.observe(viewLifecycleOwner) {
            if (it == true) {
                popBackStack()
                mDiscPresentDetailViewModel.onNavigatePopStackDone()
            }
        }

        // Go to DiscResultSearchFragment
        mDiscPresentDetailViewModel.navigateToDiscResultSearch.observe(viewLifecycleOwner) {
            it?.let { discPresent ->
                goToDiscResultSearchFragment(discPresent)
                mDiscPresentDetailViewModel.onNavigateToDiscResultSearchDone()
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

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun goToDiscResultSearchFragment(discPresent: DiscPresent) {
        materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
        findNavController().navigate(
            DiscPresentDetailFragmentDirections
                .actionDiscPresentDetailFragmentToDiscResultSearchFragment(discPresent)
        )
    }
}