package com.hausding.challenge.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hausding.challenge.databinding.RowDataBinding
import com.hausding.challenge.viewmodel.ConversionRateViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Adapter for coin conversion rates
 * @property scope Coroutine scope for data diff calculations and updates
 * @property calcJob Job for calculating data diff
 * @property updateJob Job for updating data
 * @property conversionRates List of ConversionRate objects
 */
class DataAdapter : RecyclerView.Adapter<DataAdapter.DataRowViewHolder>() {
    private val scope = MainScope()
    private var calcJob : Job? = null
    private var updateJob : Job? = null

    private var conversionRates: List<ConversionRateViewData> = listOf()
    fun updateData(newConversionRates: List<ConversionRateViewData>) {
        calcJob?.cancel()
        updateJob?.cancel()
        val currList = this.conversionRates.toList()
        calcJob = scope.launch(Dispatchers.IO) {
            val diffResult = DiffUtil.calculateDiff(DataDiffCallback(currList, newConversionRates))
            updateJob = scope.launch(Dispatchers.Main) {
                conversionRates = newConversionRates
                diffResult.dispatchUpdatesTo(this@DataAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRowViewHolder {
        return DataRowViewHolder(RowDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DataRowViewHolder, position: Int) {

        holder.bindValues(conversionRates[position].timestamp, conversionRates[position].rate)
    }

    override fun getItemCount(): Int {
        return conversionRates.size
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
