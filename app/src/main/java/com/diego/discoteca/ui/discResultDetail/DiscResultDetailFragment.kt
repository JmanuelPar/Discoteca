package com.diego.discoteca.ui.discResultDetail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diego.discoteca.R
import com.diego.discoteca.activity.MainActivity
import com.diego.discoteca.activity.DiscotecaApplication
import com.diego.discoteca.databinding.FragmentDiscResultDetailBinding
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class DiscResultDetailFragment : Fragment() {

    private val mDiscResultDetailViewModel: DiscResultDetailViewModel by viewModels {
        val arguments = DiscResultDetailFragmentArgs.fromBundle(requireArguments())
        DiscResultDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).repository,
            discResultDetail = arguments.discResultDetail
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
        val binding: FragmentDiscResultDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_disc_result_detail,
            container,
            false
        )

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discResultDetailViewModel = mDiscResultDetailViewModel
        }

        mDiscResultDetailViewModel.navigateToDisc.observe(viewLifecycleOwner) {
            it?.let { pair ->
                goToDiscFragment(
                    uiText = pair.first,
                    id = pair.second
                )
                mDiscResultDetailViewModel.onNavigateToDiscDone()
            }
        }

        mDiscResultDetailViewModel.navigatePopStack.observe(viewLifecycleOwner) {
            if (it == true) {
                navigatePopStack()
                mDiscResultDetailViewModel.onNavigatePopStackDone()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun goToDiscFragment(uiText: UIText, id: Long) {
        (activity as MainActivity).navigateTo(
            DiscResultDetailFragmentDirections
                .actionDiscResultDetailFragmentToDiscFragment(
                    uiText = uiText,
                    idAdded = id
                )
        )
    }

    private fun navigatePopStack() {
        (activity as MainActivity).navigatePopStack()
    }
}