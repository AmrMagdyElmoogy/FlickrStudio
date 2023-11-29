package com.example.flickrstudio.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ActionMenuView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flickrstudio.R
import com.example.flickrstudio.databinding.FragmentFlickrGalleryBinding
import com.example.flickrstudio.util.changeVisibility
import com.example.flickrstudio.viewModel.FlickrViewModel
import com.example.flickrstudio.viewModel.States
import com.example.flickrstudio.workmanager.PollWorkManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [FlickrGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val TAG_SUBMIT = "Submit query"
const val POLLING_WORK = "PollWorkState"
const val TAG_TEXTING = "texting"

class FlickrGalleryFragment : Fragment() {

    private var _binding: FragmentFlickrGalleryBinding? = null
    private lateinit var recyclerView: RecyclerView

    private var searchView: SearchView? = null
    private lateinit var circularProgrssBar: ProgressBar
    private lateinit var notFoundImage: ImageView
    private var pollingMenuItem: MenuItem? = null
    private val args by navArgs<FlickrGalleryFragmentArgs>()

    private val query by lazy {
        args.query
    }
    private val binding: FragmentFlickrGalleryBinding
        get() = checkNotNull(_binding) {
            "Your instance of Flickr Gallery binding is null!"
        }

    private val viewModel by viewModels<FlickrViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlickrGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                recyclerView.adapter = GalleryRecycleAdapter(listOf())
                viewModel.changeToEmptyState()
                true
            }

            R.id.menu_item_toggle_work -> {
                viewModel.togglePolling()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_items, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_work)

        searchView = searchItem.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG_SUBMIT, "QueryTextSubmitted : ${query.toString()}")
                query?.let {
                    viewModel.setQuery(it)
                }
                closeKeyboardAfterSubmitting()
                disableSearchViewUntilFetchingData()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG_TEXTING, "QueryTextChanged : ${newText.toString()}")
                return false
            }
        })
    }

    private fun disableSearchViewUntilFetchingData() {
        searchView?.isFocusable = false
    }

    private fun closeKeyboardAfterSubmitting() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(searchView?.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerView = photoGallery
            circularProgrssBar = progressBar
            notFoundImage = notFound
        }
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it.states) {
                        States.LOADING -> {
                            circularProgrssBar.changeVisibility(View.VISIBLE)
                            recyclerView.changeVisibility(View.INVISIBLE)
                            notFoundImage.changeVisibility(View.INVISIBLE)
                        }

                        States.EMPTY -> {
                            notFoundImage.changeVisibility(View.VISIBLE)
                            recyclerView.changeVisibility(View.INVISIBLE)
                            circularProgrssBar.changeVisibility(View.INVISIBLE)
                        }

                        States.SUCCESS -> {
                            recyclerView.changeVisibility(View.VISIBLE)
                            notFoundImage.changeVisibility(View.INVISIBLE)
                            circularProgrssBar.changeVisibility(View.INVISIBLE)
                            searchView?.isFocusable = true
                        }
                    }
                    updatePollingState(it.isPolling)
                    recyclerView.adapter = GalleryRecycleAdapter(it.images)
                    searchView?.setQuery(it.query, false)
                }

            }
        }
    }

    private fun updatePollingState(polling: Boolean) {
        pollingMenuItem?.title = if (polling) {
            getString(R.string.stop_polling)
        } else {
            getString(R.string.start_polling)
        }

        if (polling) {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            val workRequest =
                PeriodicWorkRequestBuilder<PollWorkManager>(15, TimeUnit.MINUTES).setConstraints(
                    constraints
                ).build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                POLLING_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLLING_WORK)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView = null
        pollingMenuItem = null
    }
}