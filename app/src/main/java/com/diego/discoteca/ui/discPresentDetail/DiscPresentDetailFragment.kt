package com.diego.discoteca.ui.discPresentDetail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.data.model.DiscPresent
import com.diego.discoteca.databinding.FragmentDiscPresentDetailBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.materialSharedAxisExitReenterTransition
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class DiscPresentDetailFragment : Fragment() {

    private val mDiscPresentDetailViewModel: DiscPresentDetailViewModel by viewModels {
        val arguments = DiscPresentDetailFragmentArgs.fromBundle(requireArguments())
        DiscPresentDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discPresent = arguments.discPresent
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentDiscPresentDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_disc_present_detail,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discPresentDetailViewModel = mDiscPresentDetailViewModel
        }

        // PopStack
        mDiscPresentDetailViewModel.navigatePopStack.observe(viewLifecycleOwner) {
            if (it == true) {
                navigatePopStack()
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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun navigatePopStack() {
        (activity as MainActivity).navigatePopStack()
    }

    private fun goToDiscResultSearchFragment(discPresent: DiscPresent) {
        materialSharedAxisExitReenterTransition(MaterialSharedAxis.Z)
        (activity as MainActivity).navigateTo(
            DiscPresentDetailFragmentDirections
                .actionDiscPresentDetailFragmentToDiscResultSearchFragment(discPresent)
        )
    }
}