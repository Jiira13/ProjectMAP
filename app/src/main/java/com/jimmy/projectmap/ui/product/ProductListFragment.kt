// app/src/main/java/com/jimmy/projectmap/ui/product/ProductListFragment.kt
package com.jimmy.projectmap.ui.product

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.jimmy.projectmap.R
import com.jimmy.projectmap.core.util.ConfirmDialog

class ProductListFragment : Fragment(R.layout.fragment_product_list) {

    // ⬅️ INI YANG NGEBUAT 'vm'
    private val vm: ProductListViewModel by viewModels()

    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddProduct)
        val search = view.findViewById<TextInputEditText>(R.id.etSearch)

        adapter = ProductAdapter(
            onClick = { product ->
                findNavController().navigate(
                    R.id.productDetailFragment,
                    bundleOf("productId" to product.id)
                )
            },
            onLongClick = { product ->
                // opsional: hapus/edit
            }
        )

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        // ⬅️ OBSERVE LIVE DATA DARI VM
        vm.productsLive.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // (opsional) filter lokal
        search?.addTextChangedListener { txt ->
            val all = vm.productsLive.value.orEmpty()
            val q = txt?.toString().orEmpty().lowercase()
            adapter.submitList(if (q.isBlank()) all else all.filter { it.name.lowercase().contains(q) })
        }

        fab?.setOnClickListener {
            // TODO: navigate ke form
        }
    }
}