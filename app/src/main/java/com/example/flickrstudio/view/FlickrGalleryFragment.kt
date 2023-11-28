package com.example.flickrstudio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ActionMenuView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickrstudio.R
import com.example.flickrstudio.databinding.FragmentFlickrGalleryBinding
import com.example.flickrstudio.viewModel.FlickrViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [FlickrGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

const val TAG_SUBMIT = "Submit query"

const val TAG_TEXTING = "texting"

class FlickrGalleryFragment : Fragment() {

    private var _binding: FragmentFlickrGalleryBinding? = null
    private lateinit var recyclerView: RecyclerView

    private var searchView: SearchView? = null
    private val binding: FragmentFlickrGalleryBinding
        get() = checkNotNull(_binding) {
            "Your instance of Flickr Gallery binding is null!"
        }

    private val viewModel by viewModels<FlickrViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlickrGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.setQuery("")
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
        searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG_SUBMIT, "QueryTextSubmitted : ${query.toString()}")
                query?.let {
                    viewModel.setQuery(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG_TEXTING, "QueryTextChanged : ${newText.toString()}")
                return false
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.photoGallery
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    recyclerView.adapter = GalleryRecycleAdapter(it.images)
                    searchView?.setQuery(it.query, false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchView = null
    }
}