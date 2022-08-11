package com.diego.discoteca.ui.discResultScan

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import com.diego.discoteca.R
import com.diego.discoteca.activity.DiscotecaApplication
import com.diego.discoteca.activity.MainActivity
import com.diego.discoteca.adapter.DiscResultAdapter
import com.diego.discoteca.adapter.DiscsLoadStateAdapter
import com.diego.discoteca.adapter.Listener
import com.diego.discoteca.databinding.FragmentDiscResultScanBinding
import com.diego.discoteca.model.DiscResultDetail
import com.diego.discoteca.network.DiscogsApi
import com.diego.discoteca.repository.DiscogsRepository
import com.diego.discoteca.util.*
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DiscResultScanFragment : Fragment() {

    private val mDiscResultScanViewModel: DiscResultScanViewModel by viewModels {
        val arguments = DiscResultScanFragmentArgs.fromBundle(requireArguments())
        DiscResultScanViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).repository,
            discogsRepository = DiscogsRepository(DiscogsApi.retrofitService),
            discResultScan = arguments.discResultScan
        )
    }

    private lateinit var binding: FragmentDiscResultScanBinding
    private lateinit var adapterDiscResult: DiscResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.Z)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_disc_result_scan,
            container,
            false
        )

        adapterDiscResult = DiscResultAdapter(Listener { view, disc ->
            mDiscResultScanViewModel.onDiscResultClicked(view, disc)
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
                goToDiscResultDetailFragment(view = pair.first, discResultDetail = pair.second)
                mDiscResultScanViewModel.onDiscResultDetailClickedDone()
            }
        }

        mDiscResultScanViewModel.navigateToDiscDetail.observe(viewLifecycleOwner) { it ->
            it?.let { pair ->
                goToDiscDetailFragment(view = pair.first, id = pair.second)
                mDiscResultScanViewModel.onDiscDetailClickedDone()
            }
        }

        mDiscResultScanViewModel.navigateTo.observe(viewLifecycleOwner) {
            it?.let { discItemCode ->
                when (discItemCode) {
                    API -> goToDiscFragment()
                    DATABASE -> goToInformationFragment()
                }
                mDiscResultScanViewModel.onNavigateToDone()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun goToDiscResultDetailFragment(view: View, discResultDetail: DiscResultDetail) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName =
            getString(R.string.disc_detail_card_transition_name)
        (activity as MainActivity).navigateToWithExtras(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscResultDetailFragment(discResultDetail),
            FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun goToDiscDetailFragment(view: View, id: Long) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName =
            getString(R.string.disc_detail_card_transition_name)
        (activity as MainActivity).navigateToWithExtras(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscDetailFragment(id),
            FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun goToDiscFragment() {
        (activity as MainActivity).navigateTo(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToDiscFragment(
                    uiText = UIText.NoDisplay,
                    idAdded = -1L
                )
        )
    }

    private fun goToInformationFragment() {
        (activity as MainActivity).navigateTo(
            DiscResultScanFragmentDirections
                .actionDiscResultScanFragmentToInfoFragment()
        )
    }
}