package com.jimmy.projectmap.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jimmy.projectmap.core.model.Product
import com.jimmy.projectmap.R

class ProductAdapter(
    private val onClick: (Product) -> Unit = {},
    private val onLongClick: (Product) -> Unit = {}
) : ListAdapter<Product, ProductAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(o: Product, n: Product) = o.id == n.id
        override fun areContentsTheSame(o: Product, n: Product) = o == n
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvStock: TextView = view.findViewById(R.id.tvStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = getItem(position)
        holder.tvName.text = product.name
        holder.tvStock.text = "Stok: ${product.stock} • Rp${product.price}"

        holder.itemView.setOnClickListener { onClick(product) }
        holder.itemView.setOnLongClickListener {
            onLongClick(product)
            true
        }
    }
}