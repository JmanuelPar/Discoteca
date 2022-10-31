package com.diego.discoteca.ui.discResultScan

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.adapter.DiscResultAdapter
import com.diego.discoteca.adapter.DiscsLoadStateAdapter
import com.diego.discoteca.adapter.Listener
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.databinding.FragmentDiscResultScanBinding
import com.diego.discoteca.util.Destination
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.materialElevationScaleExitReenterTransition
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DiscResultScanFragment : Fragment(R.layout.fragment_disc_result_scan), MenuProvider {

    private val args: DiscResultScanFragmentArgs by navArgs()
    private val mDiscResultScanViewModel: DiscResultScanViewModel by viewModels {
        DiscResultScanViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discResultScan = args.discResultScan
        )
    }

    private lateinit var binding: FragmentDiscResultScanBinding
    private lateinit var adapterDiscResult: DiscResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.Z)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDiscResultScanBinding.bind(view)
        adapterDiscResult = DiscResultAdapter(Listener { v, disc ->
            mDiscResultScanViewModel.onDiscResultClicked(v, disc)
        })

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discResultScanViewModel = mDiscResultScanViewModel
            rvListDiscResultScan.apply {
                adapter = adapterDiscResult.withLoadStateHeaderAndFooter(
                    header = DiscsLoadStateAdapter { adapterDiscResult.retry() },
                    footer = DiscsLoadStateAdapter { adapterDiscResult.retry() }
                )
                setHasFixedSize(true)
            }
        }

        configure()

        mDiscResultScanViewModel.buttonRetry.observe(viewLifecycleOwner) {
            if (it == true) {
                adapterDiscResult.retry()
                mDiscResultScanViewModel.onButtonRetryClickedDone()
            }
        }

        mDiscResultScanViewModel.navigateToDiscResultDetail.observe(viewLifecycleOwner) {
            it?.let { pair ->
                goToDiscResultDetailFragment(
                    view = pair.first,
                    discResultDetail = pair.second
                )
                mDiscResultScanViewModel.onDiscResultDetailClickedDone()
            }
        }

        mDiscResultScanViewModel.navigateToDiscDetail.observe(viewLifecycleOwner) {
            it?.let { pair ->
                goToDiscDetailFragment(
                    view = pair.first,
                    id = pair.second
                )
                mDiscResultScanViewModel.onDiscDetailClickedDone()
            }
        }

        mDiscResultScanViewModel.buttonBack.observe(viewLifecycleOwner) { destination ->
            if (destination != Destination.NONE) {
                when (destination) {
                    Destination.API -> goToDiscFragment()
                    Destination.DATABASE -> goToInformationFragment()
                    else -> goToDiscFragment()
                }

                mDiscResultScanViewModel.onButtonBackClickedDone()
            }
        }

        setupMenu()

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    private fun configure() {
        viewLifecycleOwner.lifecycleScope.launch {
            mDiscResultScanViewModel.pagingDataFlow
                .collectLatest(adapterDiscResult::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapterDiscResult.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapterDiscResult.itemCount == 0
                val isAdapterNotEmpty = adapterDiscResult.itemCount != 0

                binding.progressBarSearch.isVisible =
                    loadState.source.refresh is LoadState.Loading
                binding.layoutNoScanResult.isVisible = isListEmpty
                binding.rvListDiscResultScan.isVisible = !isListEmpty
                binding.layoutResult.isVisible = isAdapterNotEmpty

                mDiscResultScanViewModel.updateNbDisc(adapterDiscResult.snapshot())
                if (isAdapterNotEmpty) mDiscResultScanViewModel.updateTotal(adapterDiscResult.itemCount)

                val errorState = loadState.refresh as? LoadState.Error
                errorState?.let { loadStateError ->
                    val errorMessage = when (loadStateError.error) {
                        is IOException -> getString(R.string.no_connect_message)
                        is HttpException -> String.format(
                            getString(R.string.error_result_message_retry),
                            loadStateError.error.localizedMessage
                        )
                        else -> getString(R.string.error_result_message_unknown_retry)
                    }

                    mDiscResultScanViewModel.showErrorLayout(errorMessage)
                }
            }
        }
    }

    private fun setupMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )
    }

    private fun goToDiscResultDetailFragment(view: View, discResultDetail: DiscResultDetail) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName = getString(R.string.disc_detail_card_transition_name)
        view.findNavController().navigate(
            directions = DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscResultDetailFragment(discResultDetail),
            navigatorExtras = FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun goToDiscDetailFragment(view: View, id: Long) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName = getString(R.string.disc_detail_card_transition_name)
        view.findNavController().navigate(
            directions = DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscDetailFragment(id),
            navigatorExtras = FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun goToDiscFragment() {
        findNavController().navigate(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscFragment(
                    uiText = UIText.NoDisplay,
                    idAdded = -1L
                )
        )
    }

    private fun goToInformationFragment() {
        findNavController().navigate(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToInfoFragment()
        )
    }
}