package com.diego.discoteca.ui.discPresent

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.adapter.DiscPresentAdapter
import com.diego.discoteca.adapter.Listener
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.databinding.FragmentDiscPresentBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.materialElevationScaleExitReenterTransition
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.diego.discoteca.util.materialSharedAxisExitReenterTransition
import com.google.android.material.transition.MaterialSharedAxis

class DiscPresentFragment : Fragment(R.layout.fragment_disc_present), MenuProvider {

    private val args: DiscPresentFragmentArgs by navArgs()
    private val mDiscPresentViewModel: DiscPresentViewModel by viewModels {
        DiscPresentViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discPresent = args.discPresent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.Z)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDiscPresentBinding.bind(view)
        val adapterDiscPresent = DiscPresentAdapter(Listener { v, disc ->
            val discPresent = mDiscPresentViewModel.discItem

            // Here, we do a search with the disk chosen by the user
            discPresent.discAdd.apply {
                id = disc.id
                name = disc.name
                title = disc.title
                year = disc.year
            }

            goToDiscPresentDetailFragment(
                view = v,
                discPresent = discPresent
            )
        })

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discPresentViewModel = mDiscPresentViewModel
            rvListDiscPresent.apply {
                adapter = adapterDiscPresent
                setHasFixedSize(true)
            }
        }

        mDiscPresentViewModel.listDisc.observe(viewLifecycleOwner) { listDisc ->
            adapterDiscPresent.submitList(listDisc)
        }

        mDiscPresentViewModel.navigateToDisc.observe(viewLifecycleOwner) {
            it?.let { pair ->
                navigateTo(
                    DiscPresentFragmentDirections
                        .actionDiscPresentFragmentToDiscFragment(
                            uiText = pair.first,
                            idAdded = pair.second
                        )
                )
                mDiscPresentViewModel.onNavigateToDiscDone()
            }
        }

        mDiscPresentViewModel.navigateToDiscResultSearch.observe(viewLifecycleOwner) {
            it?.let { discPresent ->
                materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
                navigateTo(
                    DiscPresentFragmentDirections
                        .actionDiscPresentFragmentToDiscResultSearchFragment(discPresent)
                )
                mDiscPresentViewModel.onNavigateToDiscResultSearchDone()
            }
        }

        setupMenu()

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
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

    private fun goToDiscPresentDetailFragment(
        view: View,
        discPresent: DiscPresent
    ) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName = getString(R.string.disc_detail_card_transition_name)
        (activity as MainActivity).navigateToWithExtras(
            directions = DiscPresentFragmentDirections
                .actionDiscPresentFragmentToDiscPresentDetailFragment(discPresent),
            extras = FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun navigateTo(navDirections: NavDirections) {
        (activity as MainActivity).navigateTo(navDirections)
    }
}