package com.example.flickrstudio.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flickrstudio.databinding.ListItemQueryBinding

class QueriesRecycleAdapter(
    private val listOfQueries: List<String>,
    val onQueryClickListener: (String)-> Unit = {}
) : RecyclerView.Adapter<QueriesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemQueryBinding.inflate(inflater, parent, false)
        return QueriesViewHolder(binding){
            onQueryClickListener(it)
        }
    }

    override fun getItemCount(): Int {
        return listOfQueries.size
    }

    override fun onBindViewHolder(holder: QueriesViewHolder, position: Int) {
        val query = listOfQueries[position]
        holder.bind(query)
    }
}