package com.hausding.challenge.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hausding.challenge.databinding.RowHeaderBinding

/**
 * Adapter for table header
 */
class HeaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : RecyclerView.ViewHolder(RowHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false).root) {}

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount() = 1
}