package com.example.flickrstudio.view

import androidx.recyclerview.widget.RecyclerView
import com.example.flickrstudio.databinding.FragmentListQueriesBinding
import com.example.flickrstudio.databinding.ListItemQueryBinding

class QueriesViewHolder(
    private val binding: ListItemQueryBinding,
    val onQueryClickListener: (String) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            val view = ListItemQueryBinding.bind(it)
            onQueryClickListener(view.query.text.toString())
        }
    }

    fun bind(textQuery: String) {
        binding.query.text = textQuery
    }

}