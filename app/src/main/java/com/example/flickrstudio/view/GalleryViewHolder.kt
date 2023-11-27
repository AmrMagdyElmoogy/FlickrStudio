package com.example.flickrstudio.view

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.flickrstudio.R
import com.example.flickrstudio.api.GalleryItem
import com.example.flickrstudio.databinding.ListItemGalleryBinding

class GalleryViewHolder(
    private val binding: ListItemGalleryBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(gallery: GalleryItem) {
        val imageView = binding.itemGallery
        imageView.load(gallery.url) {
            placeholder(R.drawable.placeholder)
        }
    }
}