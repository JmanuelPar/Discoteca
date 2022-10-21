package com.diego.discoteca.ui.information

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.adapter.CountFormatMediaAdapter
import com.diego.discoteca.databinding.FragmentInformationBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.Destination
import com.google.android.material.transition.MaterialFadeThrough

class InformationFragment : Fragment(R.layout.fragment_information), MenuProvider {

    private val mInformationViewModel: InformationViewModel by viewModels {
        InformationViewModelFactory(
            (requireContext().applicationContext as DiscotecaApplication).discsRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInformationBinding.bind(view)
        val adapterCountFormatMedia = CountFormatMediaAdapter()
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            informationViewModel = mInformationViewModel
            rvListFormatMedia.apply {
                adapter = adapterCountFormatMedia
                setHasFixedSize(true)
            }
        }

        mInformationViewModel.countFormatMediaList.observe(viewLifecycleOwner) { list ->
            adapterCountFormatMedia.submitList(list)
        }

        mInformationViewModel.buttonSearchScan.observe(viewLifecycleOwner) {
            if (it == true) {
                goToScanBarcodeFragment()
                mInformationViewModel.onButtonSearchScanClickedDone()
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

    private fun goToScanBarcodeFragment() {
        (activity as MainActivity).navigate(
            InformationFragmentDirections.actionInfoFragmentToScanBarcodeFragment(Destination.DATABASE)
        )
    }
}