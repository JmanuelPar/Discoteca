package com.diego.discoteca.ui.information

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diego.discoteca.R
import com.diego.discoteca.activity.MainActivity
import com.diego.discoteca.activity.DiscotecaApplication
import com.diego.discoteca.adapter.CountFormatMediaAdapter
import com.diego.discoteca.databinding.FragmentInformationBinding
import com.diego.discoteca.util.Destination
import com.google.android.material.transition.MaterialFadeThrough

class InformationFragment : Fragment() {

    private val mInformationViewModel: InformationViewModel by viewModels {
        InformationViewModelFactory((requireContext().applicationContext as DiscotecaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInformationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_information,
            container,
            false
        )

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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun goToScanBarcodeFragment() {
        (activity as MainActivity).navigate(
            InformationFragmentDirections.actionInfoFragmentToScanBarcodeFragment(Destination.DATABASE)
        )
    }
}