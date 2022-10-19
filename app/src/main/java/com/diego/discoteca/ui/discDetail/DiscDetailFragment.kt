package com.diego.discoteca.ui.discDetail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentDiscDetailBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class DiscDetailFragment : Fragment() {

    private val mDiscDetailViewModel: DiscDetailViewModel by viewModels {
        val arguments = DiscDetailFragmentArgs.fromBundle(requireArguments())
        DiscDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discId = arguments.discId
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
        val binding: FragmentDiscDetailBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_disc_detail,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discDetailViewModel = mDiscDetailViewModel
        }

        mDiscDetailViewModel.buttonOk.observe(viewLifecycleOwner) {
            if (it == true) {
                (activity as MainActivity).navigatePopStack()
                mDiscDetailViewModel.buttonOkClickedDone()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}