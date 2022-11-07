package com.hausding.challenge.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hausding.challenge.databinding.RowDataBinding
import com.hausding.challenge.viewmodel.ConversionRateViewData

/**
 * Adapter for coin conversion rates
 * @property diffCallback DiffUtil for comparing displayed items
 * @property mDiffer AsyncListDiffer for storing and calculating displayed item changes
 */
class DataAdapter : RecyclerView.Adapter<DataAdapter.DataRowViewHolder>() {
    private val diffCallback = object: DiffUtil.ItemCallback<ConversionRateViewData>() {
        override fun areItemsTheSame(
            oldItem: ConversionRateViewData,
            newItem: ConversionRateViewData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ConversionRateViewData,
            newItem: ConversionRateViewData
        ): Boolean {
            return oldItem == newItem
        }
    }
    private var mDiffer = AsyncListDiffer(this, diffCallback)

    fun updateData(newConversionRates: List<ConversionRateViewData>) {
        mDiffer.submitList(newConversionRates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRowViewHolder {
        return DataRowViewHolder(RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataRowViewHolder, position: Int) {
        val element = mDiffer.currentList[position]
        holder.bindValues(element.timestamp, element.rate)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    /**
     * ViewHolder class for data rows
     * @property rowBinding ViewBinding for data row
     */
    class DataRowViewHolder(private val rowBinding: RowDataBinding): RecyclerView.ViewHolder(rowBinding.root) {
        /**
         * Function for view data binding
         * @param date String with date of conversion rate
         * @param rate String with conversion rate
         */
        fun bindValues(date: String, rate: String) {
            rowBinding.rowDataTextDate.text = date
            rowBinding.rowDataTextRate.text = rate
        }
    }
}

private class DataDiffCallback(var oldList: List<ConversionRateViewData>, var newList: List<ConversionRateViewData>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp && oldList[oldItemPosition].rate == newList[newItemPosition].rate
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
