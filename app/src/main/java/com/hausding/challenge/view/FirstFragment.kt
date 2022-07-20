package com.hausding.challenge.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import com.hausding.challenge.R
import com.hausding.challenge.databinding.FragmentFirstBinding
import com.hausding.challenge.model.service.DataService
import com.hausding.challenge.model.service.ServiceAction
import com.hausding.challenge.view.adapter.DataAdapter
import com.hausding.challenge.view.adapter.HeaderAdapter
import com.hausding.challenge.viewmodel.FirstFragmentViewModel

/**
 * Fragment responsible for displaying coin data
 * @property _binding Internal binding for fragment view
 * @property binding Binding for fragment view
 * @property viewModel ViewModel of this fragment
 * @property concatAdapter Adapter for conversion rate data and table header row
 * @property dataAdapter Adapter for conversion rate data
 */
class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstFragmentViewModel by viewModels()

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var dataAdapter: DataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        dataAdapter = DataAdapter()
        concatAdapter = ConcatAdapter(HeaderAdapter(), dataAdapter)
        binding.fragRateList.adapter = concatAdapter
        binding.fragRateList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.fragTextfieldCoin.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectCoin(position)
            sendChangeCurrencyEvent()
        }
        binding.fragTextfieldCurrency.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectCurrency(position)
            sendChangeCurrencyEvent()
        }
        binding.fragTextfieldCurrency.setText(viewModel.selectedCurrency.value)
        setupObserver()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Setup observers for viewmodel livedata
     */
    private fun setupObserver() {
        viewModel.selectedCoin.observe(viewLifecycleOwner) {
            binding.fragTextfieldCoin.setText(it)
        }
        viewModel.selectedCurrency.observe(viewLifecycleOwner) {
            binding.fragTextfieldCurrency.setText(it)
        }
        viewModel.coins.observe(viewLifecycleOwner) { coins ->
            binding.fragTextfieldCoin.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line, coins.toTypedArray()))
        }
        viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            binding.fragTextfieldCurrency.setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line, currencies.toTypedArray()))
        }
        viewModel.rates.observe(viewLifecycleOwner) {
            dataAdapter.updateData(it)
        }
        viewModel.syncDate.observe(viewLifecycleOwner) {
            binding.fragTextDate.text = getString(R.string.sync_label, it ?: getString(R.string.sync_never))
        }
        viewModel.errorBus.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.toast_error_data, it.toString()), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Send event to data service for conversion changes
     */
    private fun sendChangeCurrencyEvent() {
        Intent(requireContext(), DataService::class.java).also {
            it.action = ServiceAction.CHANGE_CURRENCY.name
            requireActivity().startService(it)

        }
    }
}