package com.diego.discoteca.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diego.discoteca.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var listener: MyBottomSheetListener
    private lateinit var binding: LayoutBottomSheetBinding

    interface MyBottomSheetListener {
        fun yesButtonClicked()
        fun noButtonClicked()
    }

    companion object {
        const val TAG = "My BottomSheet"
        private const val KEY_ARTIST_NAME = "KEY_ARTIST_NAME"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_YEAR = "KEY_YEAR"

        fun newInstance(
            artistName: String,
            title: String,
            year: String
        ): MyBottomSheetDialogFragment {
            val args = Bundle()
            args.apply {
                putString(KEY_ARTIST_NAME, artistName)
                putString(KEY_TITLE, title)
                putString(KEY_YEAR, year)
            }
            val fragment = MyBottomSheetDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnButtonsClick(listener: MyBottomSheetListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutBottomSheetBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupClickListeners()
        expandedBottomSheet()
    }

    private fun setupView() {
        binding.apply {
            tvArtistNameAdd.text = arguments?.getString(KEY_ARTIST_NAME)
            tvTitleAdd.text = arguments?.getString(KEY_TITLE)
            tvYearAdd.text = arguments?.getString(KEY_YEAR)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            buttonYes.setOnClickListener {
                listener.yesButtonClicked()
                dismiss()
            }
            buttonNo.setOnClickListener {
                listener.noButtonClicked()
                dismiss()
            }
        }
    }

    private fun expandedBottomSheet() {
        dialog?.setOnShowListener {
            BottomSheetBehavior.from(binding.bottomSheetLayout).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }
}