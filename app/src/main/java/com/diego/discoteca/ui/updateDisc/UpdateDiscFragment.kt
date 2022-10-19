package com.diego.discoteca.ui.updateDisc

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentUpdateDiscBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.diego.discoteca.util.showBottomSheetModal
import com.diego.discoteca.util.showDialogTitle
import com.google.android.material.transition.MaterialSharedAxis

class UpdateDiscFragment : Fragment() {

    private val mUpdateDiscViewModel: UpdateDiscViewModel by viewModels {
        val arguments = UpdateDiscFragmentArgs.fromBundle(requireArguments())
        UpdateDiscViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discId = arguments.discId
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
            it?.let { disc ->
                childFragmentManager.showBottomSheetModal(
                    artistName = disc.name,
                    title = disc.title,
                    year = disc.year
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
        requireContext().showDialogTitle(
            title = title,
            message = message
        )
    }

    private fun goToDiscFragment(directions: NavDirections) {
        (activity as MainActivity).navigateTo(directions)
    }
}