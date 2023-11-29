package com.example.flickrstudio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.flickrstudio.databinding.FragmentListQueriesBinding
import com.example.flickrstudio.viewModel.FlickrViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QueryListFragment : Fragment() {
    private lateinit var binding: FragmentListQueriesBinding
    private lateinit var recycleView: RecyclerView
    private val viewModel by viewModels<FlickrViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListQueriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            recycleView = listQueries
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listOfStoredQueries.collectLatest {
                    recycleView.adapter = QueriesRecycleAdapter(it) { query ->
                        findNavController().navigate(
                            QueryListFragmentDirections.BackToImagesFragment(
                                query
                            )
                        )
                    }
                }
            }
        }

    }
}