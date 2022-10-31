package com.diego.discoteca.ui.discDetail

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.databinding.FragmentDiscDetailBinding
import com.diego.discoteca.util.themeColor
import com.google.android.material.transition.MaterialContainerTransform

class DiscDetailFragment : Fragment(R.layout.fragment_disc_detail), MenuProvider {

    private val args: DiscDetailFragmentArgs by navArgs()
    private val mDiscDetailViewModel: DiscDetailViewModel by viewModels {
        DiscDetailViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discId = args.discId
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
        val binding = FragmentDiscDetailBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discDetailViewModel = mDiscDetailViewModel
        }

        mDiscDetailViewModel.buttonOk.observe(viewLifecycleOwner) {
            if (it == true) {
                popBackStack()
                mDiscDetailViewModel.buttonOkClickedDone()
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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }
}