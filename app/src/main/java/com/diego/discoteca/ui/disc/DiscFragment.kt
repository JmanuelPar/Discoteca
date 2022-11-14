package com.diego.discoteca.ui.disc

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diego.discoteca.DiscotecaApplication
import com.diego.discoteca.R
import com.diego.discoteca.adapter.DiscAdapter
import com.diego.discoteca.data.SortOrder
import com.diego.discoteca.data.domain.Disc
import com.diego.discoteca.databinding.FragmentDiscBinding
import com.diego.discoteca.ui.activity.MainActivity
import com.diego.discoteca.util.UIText
import com.diego.discoteca.util.delayedTransition
import com.diego.discoteca.util.materialElevationScaleExitReenterTransition
import com.diego.discoteca.util.showDialogMessageAction
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DiscFragment : Fragment(R.layout.fragment_disc), MenuProvider, DiscAdapter.DiscListener {

    private val args: DiscFragmentArgs by navArgs()
    private val mDiscViewModel: DiscViewModel by viewModels {
        DiscViewModelFactory(
            repository = (requireContext().applicationContext as DiscotecaApplication).discsRepository,
            preferencesManager = (requireContext().applicationContext as DiscotecaApplication).preferencesManager,
            uiText = args.uiText,
            idAdded = args.idAdded
        )
    }

    private lateinit var binding: FragmentDiscBinding
    private lateinit var mRvListDiscs: RecyclerView
    private lateinit var myLayoutManager: GridLayoutManager
    private lateinit var discAdapter: DiscAdapter
    private lateinit var menuHost: MenuHost
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDiscBinding.bind(view)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setRecyclerView()

        // List Discs
        mDiscViewModel.listDiscs.observe(viewLifecycleOwner) { listDiscs ->
            if (listDiscs.isNotEmpty()) mDiscViewModel.scrollToPosition(listDiscs)
            discAdapter.configureAndSubmitList(listDiscs)
        }

        // Scroll to position
        mDiscViewModel.scrollToPosition.observe(viewLifecycleOwner) {
            it?.let { position ->
                mRvListDiscs.post { mRvListDiscs.scrollToPosition(position) }
                mDiscViewModel.onScrollToPositionDone()
            }
        }

        // List / Grid mode recyclerview
        mDiscViewModel.gridMode.observe(viewLifecycleOwner) { gridMode ->
            myLayoutManager.spanCount = if (gridMode) 2 else 1
            delayedTransition(
                sceneRoot = mRvListDiscs,
                transition = MaterialFadeThrough()
            )
            mRvListDiscs.adapter = discAdapter
            setBottomBarFab()
        }

        // Delete Dialog
        mDiscViewModel.buttonDeleteDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                showDialogMessageAction(
                    message = getString(R.string.answer_delete),
                    positiveButton = getString(R.string.delete)
                ) {
                    mDiscViewModel.yesDeleteDiscClicked(id)
                }
                mDiscViewModel.buttonDeleteDiscClickedDone()
            }
        }

        // Update Dialog
        mDiscViewModel.buttonUpdateDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                showDialogMessageAction(
                    message = getString(R.string.answer_modify),
                    positiveButton = getString(R.string.modify)
                ) {
                    mDiscViewModel.yesUpdateDiscClicked(id)
                }
                mDiscViewModel.buttonUpdateDiscClickedDone()
            }
        }

        // Go to UpdateFragment
        mDiscViewModel.navigateToUpdateDisc.observe(viewLifecycleOwner) {
            it?.let { id ->
                goToUpdateFragment(
                    DiscFragmentDirections
                        .actionDiscFragmentToUpdateDiscFragment(id)
                )
                mDiscViewModel.yesUpdateDiscClickedDone()
            }
        }

        // Show SnackBar
        mDiscViewModel.showSnackBar.observe(viewLifecycleOwner) {
            it?.let { uiText ->
                if (uiText != UIText.NoDisplay) {
                    showSnackBar(uiText)
                }
                mDiscViewModel.onShowSnackBarDone()
            }
        }

        setupMenu()

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onPause() {
        super.onPause()
        viewLifecycleOwner.lifecycleScope.launch {
            val searchQuery = mDiscViewModel.searchQueryFlow.first()
            mDiscViewModel.updatePendingQuery(searchQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val pendingQuery = mDiscViewModel.pendingQuery.value
        when {
            pendingQuery != null && pendingQuery.isEmpty() -> mDiscViewModel.updateIsSearch(false)
            else -> mDiscViewModel.updateIsSearch(true)
        }
        if (this::searchView.isInitialized) searchView.setOnQueryTextListener(null)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_disc_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val sortItem = menu.findItem(R.id.sort)
        val gridItem = menu.findItem(R.id.grid)
        val settingsThemeItem = menu.findItem(R.id.theme_settings)
        setItemSortMode(menu)
        setItemGridMode(gridItem)

        searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                mDiscViewModel.updateIsSearch(true)
                settingsThemeItem.isVisible = false
                hideBottomBarHideFab()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mDiscViewModel.updateIsSearch(false)
                mDiscViewModel.updatePendingQueryDone()
                if (activity is MainActivity) {
                    delayedTransition(
                        (activity as MainActivity).getToolbar(),
                        MaterialFade().apply { duration = 150L }
                    )
                }
                menuHost.invalidateMenu()
                showBottomBarShowFab()
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    mDiscViewModel.updateSearch(newText)
                }
                return true
            }
        })

        // Show / hide menu item
        mDiscViewModel.isDisplayItem.observe(viewLifecycleOwner) { isDisplayItem ->
            searchItem.isVisible = isDisplayItem
            sortItem.isVisible = isDisplayItem
            gridItem.isVisible = isDisplayItem
        }

        // Search text
        mDiscViewModel.pendingQuery.observe(viewLifecycleOwner) { query ->
            if (query.isNotEmpty() && (isVisible && isAdded)) {
                searchItem.expandActionView()
                searchView.clearFocus()
                searchView.setQuery(query, false)
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.sort_by_name -> {
                menuItem.isChecked = !menuItem.isChecked
                mDiscViewModel.onSortOrderSelected((SortOrder.BY_NAME))
                true
            }
            R.id.sort_by_title -> {
                menuItem.isChecked = !menuItem.isChecked
                mDiscViewModel.onSortOrderSelected((SortOrder.BY_TITLE))
                true
            }
            R.id.sort_by_year -> {
                menuItem.isChecked = !menuItem.isChecked
                mDiscViewModel.onSortOrderSelected((SortOrder.BY_YEAR))
                true
            }
            R.id.grid -> {
                menuItem.isChecked = !menuItem.isChecked
                mDiscViewModel.onGridModeSelected(menuItem.isChecked)
                setItemGridModeOptionsSelected(menuItem)
                true
            }

            else -> false
        }
    }

    override fun onDiscClicked(view: View, disc: Disc) {
        goToDiscDetailFragment(
            view = view,
            disc = disc
        )
    }

    override fun onDiscDeleteClicked(disc: Disc) {
        mDiscViewModel.buttonDeleteDiscClicked(disc.id)
    }

    override fun onDiscUpdateClicked(disc: Disc) {
        mDiscViewModel.buttonUpdateDiscClicked(disc.id)
    }

    override fun onPopupMenuClicked(view: View, disc: Disc) {
        val popupMenu = PopupMenu(
            ContextThemeWrapper(
                requireContext(),
                R.style.PopupMenuCard
            ), view
        )
        popupMenu.menuInflater.inflate(
            R.menu.popup_menu_card,
            popupMenu.menu
        )

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    mDiscViewModel.buttonDeleteDiscClicked(disc.id)
                    true
                }
                R.id.menu_modify -> {
                    mDiscViewModel.buttonUpdateDiscClicked(disc.id)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun setRecyclerView() {
        mRvListDiscs = binding.rvListDiscs
        myLayoutManager = GridLayoutManager(mRvListDiscs.context, 1)
        discAdapter = DiscAdapter(
            listener = this,
            myLayoutManager = myLayoutManager
        )
        mRvListDiscs.apply {
            adapter = discAdapter
            discAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            layoutManager = myLayoutManager
            setHasFixedSize(true)
        }
    }

    private fun setupMenu() {
        menuHost = requireActivity()
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.STARTED
        )
    }

    private fun setItemGridMode(gridItem: MenuItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            when {
                mDiscViewModel.gridModeFlow.first() -> {
                    gridItem.isChecked = true
                    gridItem.setIcon(R.drawable.ic_grid_view)
                }
                else -> {
                    gridItem.isChecked = false
                    gridItem.setIcon(R.drawable.ic_list_view)
                }
            }
        }
    }

    private fun setItemGridModeOptionsSelected(item: MenuItem) {
        when {
            item.isChecked -> {
                item.setIcon(R.drawable.avd_list_view_to_grid_view)
                (item.icon as Animatable).start()
            }
            else -> {
                item.setIcon(R.drawable.avd_grid_view_to_list_view)
                (item.icon as Animatable).start()
            }
        }
    }

    private fun setItemSortMode(menu: Menu) {
        viewLifecycleOwner.lifecycleScope.launch {
            when (mDiscViewModel.sortOrderPreferencesFlow.first().sortOrder) {
                SortOrder.BY_NAME -> menu.findItem(R.id.sort_by_name).isChecked = true
                SortOrder.BY_TITLE -> menu.findItem(R.id.sort_by_title).isChecked = true
                SortOrder.BY_YEAR -> menu.findItem(R.id.sort_by_year).isChecked = true
            }
        }
    }

    // Go to UpdateFragment
    private fun goToUpdateFragment(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    // Go to DiscDetailFragment
    private fun goToDiscDetailFragment(view: View, disc: Disc) {
        materialElevationScaleExitReenterTransition()
        val discDetailCardTransitionName = getString(R.string.disc_detail_card_transition_name)
        view.findNavController().navigate(
            directions = DiscFragmentDirections.actionDiscFragmentToDiscDetailFragment(disc.id),
            navigatorExtras = FragmentNavigatorExtras(view to discDetailCardTransitionName)
        )
    }

    private fun showDialogMessageAction(
        message: String,
        positiveButton: String,
        action: () -> Unit
    ) {
        requireContext().showDialogMessageAction(
            message,
            positiveButton
        ) {
            action()
        }
    }

    private fun setBottomBarFab() {
        val isSearch = mDiscViewModel.isSearch.value
        when {
            isSearch != null && isSearch -> hideBottomBarHideFab()
            else -> showBottomBarShowFab()
        }
    }

    private fun showSnackBar(uiText: UIText) {
        if (activity is MainActivity) {
            val isSearch = mDiscViewModel.isSearch.value
            when {
                isSearch != null && isSearch -> {
                    (activity as MainActivity).showSnackBarNoAnchor(
                        uiText
                    )
                }
                else -> (activity as MainActivity).showSnackBar(
                    uiText = uiText,
                    anchorView = (activity as MainActivity).getFabView()
                )
            }
        }
    }

    private fun showBottomBarShowFab() {
        if (activity is MainActivity) {
            (activity as MainActivity).setBottomBarFab(true)
        }
    }

    private fun hideBottomBarHideFab() {
        if (activity is MainActivity) {
            (activity as MainActivity).hideBottomBarHideFab()
        }
    }
}