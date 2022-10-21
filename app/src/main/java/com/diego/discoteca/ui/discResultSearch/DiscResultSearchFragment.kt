package com.diego.discoteca.ui.discResultSearch

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
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.adapter.DiscResultAdapter
import com.diego.discoteca.adapter.DiscsLoadStateAdapter
import com.diego.discoteca.adapter.Listener
import com.diego.discoteca.data.model.DiscResultDetail
import com.diego.discoteca.databinding.FragmentDiscResultSearchBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.AddBy
import com.diego.discoteca.util.materialElevationScaleExitReenterTransition
import com.diego.discoteca.util.materialSharedAxisEnterReturnTransition
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DiscResultSearchFragment : Fragment(R.layout.fragment_disc_result_search), MenuProvider {

    private val args: DiscResultSearchFragmentArgs by navArgs()
    private val mDiscResultSearchViewModel: DiscResultSearchViewModel by viewModels {
        DiscResultSearchViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            discPresent = args.discPresent
        )
    }

    private lateinit var binding: FragmentDiscResultSearchBinding
    private lateinit var adapterDiscResult: DiscResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materialSharedAxisEnterReturnTransition(MaterialSharedAxis.Z)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDiscResultSearchBinding.bind(view)
        adapterDiscResult = DiscResultAdapter(Listener { v, disc ->
            goToDiscResultDetailFragment(
                view = v,
                discResultDetail = DiscResultDetail(
                    disc = disc,
                    addBy = AddBy.SEARCH
                )
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
        (activity as MainActivity).navigateToWithExtras(
            directions = DiscResultSearchFragmentDirections
                .actionDiscResultSearchFragmentToDiscResultDetailFragment(discResultDetail),
            extras = FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }
}