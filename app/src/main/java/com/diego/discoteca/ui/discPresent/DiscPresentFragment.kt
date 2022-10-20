package com.diego.discoteca.ui.discPresent

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
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

//TODO : update deprecation
@Suppress("DEPRECATION")
class DiscPresentFragment : Fragment() {

    private val mDiscPresentViewModel: DiscPresentViewModel by viewModels {
        val arguments = DiscPresentFragmentArgs.fromBundle(requireArguments())
        DiscPresentViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discPresent = arguments.discPresent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.Z)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDiscPresentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_disc_present,
            container,
            false
        )

        val adapterDiscPresent = DiscPresentAdapter(Listener { view, disc ->
            val discPresent = mDiscPresentViewModel.discItem
            // Here, we do a search with the disk chosen by the user
            discPresent.discAdd.apply {
                id = disc.id
                name = disc.name
                title = disc.title
                year = disc.year
            }
            goToDiscPresentDetailFragment(
                view = view,
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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
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