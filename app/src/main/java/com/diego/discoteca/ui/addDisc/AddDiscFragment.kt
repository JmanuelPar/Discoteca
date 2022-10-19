package com.diego.discoteca.ui.addDisc

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentAddDiscBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.*
import com.google.android.material.transition.MaterialSharedAxis

class AddDiscFragment : Fragment() {

    private val mAddDiscViewModel: AddDiscViewModel by viewModels {
        AddDiscViewModelFactory((requireContext().applicationContext as DiscotecaApplication).discsRepository)
    }

    private lateinit var binding: FragmentAddDiscBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.X)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_disc, container, false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            addDiscViewModel = mAddDiscViewModel
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

        // Show BottomSheet add disc
        mAddDiscViewModel.showBottomSheet.observe(viewLifecycleOwner) {
            it?.let { discAdd ->
                childFragmentManager.showBottomSheetModal(
                    artistName = discAdd.name,
                    title = discAdd.title,
                    year = discAdd.year
                ) {
                    mAddDiscViewModel.processingDisc(discAdd)
                }
                mAddDiscViewModel.onShowBottomSheetDone()
            }
        }

        // Go to DiscFragment
        mAddDiscViewModel.navigateToDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                goToDiscFragment(
                    AddDiscFragmentDirections
                        .actionAddDiscFragmentToDiscFragment(
                            uiText = UIText.DiscAdded,
                            idAdded = id
                        )
                )
                mAddDiscViewModel.onNavigateToDiscDone()
            }
        }

        // Go to DiscPresentFragment
        mAddDiscViewModel.navigateToDiscPresent.observe(viewLifecycleOwner) {
            it?.let { discPresent ->
                goToDiscPresentFragment(
                    AddDiscFragmentDirections
                        .actionAddDiscFragmentToDiscPresentFragment(discPresent)
                )
                mAddDiscViewModel.onNavigateToDiscPresentDone()
            }
        }

        // Go to DiscResultSearchFragment
        mAddDiscViewModel.navigateToDiscResultSearch.observe(viewLifecycleOwner) {
            it?.let { discPresent ->
                goToDiscResultSearchFragment(
                    AddDiscFragmentDirections
                        .actionAddDiscFragmentToDiscResultSearchFragment(discPresent)
                )
                mAddDiscViewModel.onNavigateToDiscResultSearchDone()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun showDialogTitle(title: String, message: String) {
        requireContext().showDialogTitle(
            title = title,
            message = message
        )
    }

    private fun goToDiscFragment(navDirections: NavDirections) {
        navigateTo(navDirections)
    }

    private fun goToDiscPresentFragment(navDirections: NavDirections) {
        materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
        navigateTo(navDirections)
    }

    private fun goToDiscResultSearchFragment(navDirections: NavDirections) {
        materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
        navigateTo(navDirections)
    }

    private fun navigateTo(navDirections: NavDirections) {
        (activity as MainActivity).navigateTo(navDirections)
    }
}