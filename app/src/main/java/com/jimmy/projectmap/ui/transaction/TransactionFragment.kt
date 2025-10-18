package com.jimmy.projectmap.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.jimmy.projectmap.core.data.firebase.FirebaseRepository
import com.jimmy.projectmap.R
import com.jimmy.projectmap.core.data.ServiceLocator
import com.jimmy.projectmap.ui.transaction.TransactionAdapter
import kotlinx.coroutines.launch


class TransactionFragment : Fragment(R.layout.fragment_transaction) {

    private lateinit var adapter: TransactionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvTransactions)
        val btn = view.findViewById<MaterialButton>(R.id.btnAddTransaction)

        adapter = TransactionAdapter()
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // Ambil repo & cek tipe sebelum cast
        val repo = ServiceLocator.repo
        val firebaseRepo = repo as? FirebaseRepository
        if (firebaseRepo == null) {
            // Kalau bukan Firebase (mis. InMemory), tampilkan kosong tapi tetap jalan
            Toast.makeText(requireContext(), "Repo bukan Firebase — transaksi live dimatikan", Toast.LENGTH_SHORT).show()
        } else {
            // Koleksi StateFlow transaksi dengan lifecycle-aware
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    firebaseRepo.transactions.collect { list ->
                        adapter.submitList(list)
                    }
                }
            }
        }

        btn.setOnClickListener {
            AddTransactionBottomSheet().show(parentFragmentManager, "AddTxn")
        }
    }
}