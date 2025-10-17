package com.jimmy.projectmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.jimmy.projectmap.adapter.TransactionAdapter
import kotlinx.coroutines.launch


class TransactionsFragment : Fragment(R.layout.fragment_transaction) {

    private val repo by lazy { ServiceLocator.repo as FirebaseRepository }
    private lateinit var adapter: TransactionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvTransactions)
        val btn = view.findViewById<MaterialButton>(R.id.btnAddTransaction)

        adapter = TransactionAdapter()
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        // observe flow via lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            repo.transactions.collect { list ->
                adapter.submitList(list)
            }
        }

        btn.setOnClickListener {
            AddTransactionBottomSheet().show(parentFragmentManager, "AddTxn")
        }
    }
}
