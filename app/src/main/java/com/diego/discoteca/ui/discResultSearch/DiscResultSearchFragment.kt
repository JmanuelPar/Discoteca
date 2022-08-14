package com.diego.discoteca.ui.discResultSearch

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
import com.diego.discoteca.databinding.FragmentDiscResultSearchBinding
import com.diego.discoteca.model.DiscResultDetail
import com.diego.discoteca.util.SEARCH
import com.diego.discoteca.util.materialElevationScaleExitReenterTransition
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException
import java.io.IOException

class DiscResultSearchFragment : Fragment() {

    private val mDiscResultSearchViewModel: DiscResultSearchViewModel by viewModels {
        val arguments = DiscResultSearchFragmentArgs.fromBundle(requireArguments())
        DiscResultSearchViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).repository,
            discPresent = arguments.discPresent
        )
    }

    private lateinit var binding: FragmentDiscResultSearchBinding
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
            R.layout.fragment_disc_result_search,
            container,
            false
        )

        adapterDiscResult = DiscResultAdapter(Listener { view, disc ->
            goToDiscResultDetailFragment(
                view = view,
                discResultDetail = DiscResultDetail(disc, SEARCH)
            )
        })

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            discResultSearchViewModel = mDiscResultSearchViewModel
            rvListDiscSearch.apply {
                adapter = adapterDiscResult.withLoadStateHeaderAndFooter(
                    header = DiscsLoadStateAdapter { adapterDiscResult.retry() },
                    footer = DiscsLoadStateAdapter { adapterDiscResult.retry() }
                )
                setHasFixedSize(true)
            }
        }

        configure()

        mDiscResultSearchViewModel.buttonRetry.observe(viewLifecycleOwner) {
            if (it == true) {
                adapterDiscResult.retry()
                mDiscResultSearchViewModel.onButtonRetryClickedDone()
            }
        }

        mDiscResultSearchViewModel.buttonBack.observe(viewLifecycleOwner) {
            if (it == true) {
                (activity as MainActivity).navigatePopStack()
                mDiscResultSearchViewModel.onButtonBackClickedDone()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun configure() {
        viewLifecycleOwner.lifecycleScope.launch {
            mDiscResultSearchViewModel.pagingDataFlow
                .collectLatest(adapterDiscResult::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapterDiscResult.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapterDiscResult.itemCount == 0
                mDiscResultSearchViewModel
                    .updateNbPresentDisc(adapterDiscResult.snapshot())
                mDiscResultSearchViewModel.updateTotalResult(adapterDiscResult.itemCount)

                binding.layoutNoSearchResult.isVisible = isListEmpty
                binding.rvListDiscSearch.isVisible = !isListEmpty
                binding.progressBarSearch.isVisible =
                    loadState.source.refresh is LoadState.Loading
                binding.layoutDiscAdd.isVisible = adapterDiscResult.itemCount != 0

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

                    mDiscResultSearchViewModel.showErrorLayout(errorMessage)
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
        val discDetailCardTransitionName = getString(R.string.disc_detail_card_transition_name)
        (activity as MainActivity).navigateToWithExtras(
            DiscResultSearchFragmentDirections
                .actionDiscResultSearchFragmentToDiscResultDetailFragment(discResultDetail),
            FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }
}