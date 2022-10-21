package com.diego.discoteca.ui.discResultDetail

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
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentDiscResultDetailBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class DiscResultDetailFragment : Fragment(R.layout.fragment_disc_result_detail), MenuProvider {

    private val args: DiscResultDetailFragmentArgs by navArgs()
    private val mDiscResultDetailViewModel: DiscResultDetailViewModel by viewModels {
        DiscResultDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discResultDetail = args.discResultDetail
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
        val binding = FragmentDiscResultDetailBinding.bind(view)
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