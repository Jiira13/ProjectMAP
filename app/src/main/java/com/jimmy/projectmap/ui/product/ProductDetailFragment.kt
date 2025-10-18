package com.jimmy.projectmap.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.jimmy.projectmap.ui.transaction.AddTransactionBottomSheet
import com.jimmy.projectmap.R
import com.jimmy.projectmap.core.data.ServiceLocator
import com.jimmy.projectmap.core.data.firebase.FirebaseRepository
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.toString

class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private val repo by lazy { ServiceLocator.repo as FirebaseRepository }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvName = view.findViewById<TextView>(R.id.tvProdName)
        val tvInfo = view.findViewById<TextView>(R.id.tvProdInfo)
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnEdit)
        val btnAddTxn = view.findViewById<MaterialButton>(R.id.btnAddTxn)

        val productId = arguments?.getLong("productId", 0L) ?: 0L
        if (productId == 0L) {
            Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        // Load sekali (cukup untuk UTS). Kalau mau benar-benar live, bisa bikin flow per-produk.
        viewLifecycleOwner.lifecycleScope.launch {
            val p = repo.getProduct(productId)
            if (p == null) {
                Toast.makeText(requireContext(), "Produk tidak ada", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return@launch
            }
            tvName.text = p.name
            tvInfo.text = "Stok: ${p.stock} • Harga: Rp ${p.price}"

            btnEdit.setOnClickListener {
                // (opsional) buka form edit kalau sudah ada
                // findNavController().navigate(R.id.productFormFragment, bundleOf("productId" to p.id))
                Toast.makeText(requireContext(), "Edit coming soon", Toast.LENGTH_SHORT).show()
            }

            btnAddTxn.setOnClickListener {
                AddTransactionBottomSheet().apply {
                    arguments = bundleOf(
                        "productId" to p.id,
                        "productName" to p.name
                    )
                }.show(parentFragmentManager, "AddTxn")
            }
        }
    }
}