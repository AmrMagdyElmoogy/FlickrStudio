package com.example.flickrstudio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickrstudio.databinding.FragmentFlickrGalleryBinding
import com.example.flickrstudio.viewModel.FlickrViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [FlickrGalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlickrGalleryFragment : Fragment() {

    private var _binding: FragmentFlickrGalleryBinding? = null
    private lateinit var recyclerView: RecyclerView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.photoGallery
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect {
                    recyclerView.adapter = GalleryRecycleAdapter(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}