package com.diego.discoteca.ui.updateDisc

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import com.diego.discoteca.R
import com.diego.discoteca.activity.MainActivity
import com.diego.discoteca.activity.MyApp
import com.diego.discoteca.databinding.FragmentUpdateDiscBinding
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.diego.discoteca.util.showDialogTitle
import com.diego.discoteca.util.showBottomSheetModal
import com.google.android.material.transition.MaterialSharedAxis

class UpdateDiscFragment : Fragment() {

    private val mUpdateDiscViewModel: UpdateDiscViewModel by viewModels {
        val arguments = UpdateDiscFragmentArgs.fromBundle(requireArguments())
        UpdateDiscViewModelFactory(
            MyApp.instance.repository,
            arguments.discId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.X)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentUpdateDiscBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_update_disc, container, false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            updateDiscViewModel = mUpdateDiscViewModel
            itDiscArtist.setEndIconOnClickListener {
                showDialogTitle(
                    getString(R.string.artist_group_name),
                    getString(R.string.information_artist_message)
                )
            }
            itDiscYear.setEndIconOnClickListener {
                showDialogTitle(
                    getString(R.string.disc_year_release),
                    getString(R.string.information_year_message)
                )
            }
        }

        // Go to DiscFragment
        mUpdateDiscViewModel.navigateToDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                goToDiscFragment(
                    UpdateDiscFragmentDirections.actionUpdateDiscFragmentToDiscFragment(
                        getString(R.string.disc_modified),
                        id
                    )
                )
                mUpdateDiscViewModel.onNavigateToDiscDone()
            }
        }

        // Show BottomSheet
        mUpdateDiscViewModel.showBottomSheet.observe(viewLifecycleOwner) {
            it?.let { disc ->
                childFragmentManager.showBottomSheetModal(
                    disc.name,
                    disc.title,
                    disc.year
                ) {
                    mUpdateDiscViewModel.updateDisc()
                }
                mUpdateDiscViewModel.showBottomSheetDone()
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
        requireContext().showDialogTitle(title, message)
    }

    private fun goToDiscFragment(directions: NavDirections) {
        (activity as MainActivity).navigateTo(directions)
    }
}