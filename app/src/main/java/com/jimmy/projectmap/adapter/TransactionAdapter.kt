package com.jimmy.projectmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jimmy.projectmap.R
import com.jimmy.projectmap.StockTransaction
import com.jimmy.projectmap.TransactionType

class TransactionAdapter : ListAdapter<StockTransaction, TransactionAdapter.VH>(DIFF) {
    object DIFF : DiffUtil.ItemCallback<StockTransaction>() {
        override fun areItemsTheSame(o: StockTransaction, n: StockTransaction) = o.id == n.id
        override fun areContentsTheSame(o: StockTransaction, n: StockTransaction) = o == n
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle = v.findViewById<TextView>(R.id.tvTxnTitle)
        val tvMeta = v.findViewById<TextView>(R.id.tvTxnMeta)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_transaction, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val tx = getItem(pos)
        val sign = when (tx.type) {
            TransactionType.IN -> "+"
            TransactionType.OUT -> "-"
            TransactionType.ADJUST -> if (tx.qty >= 0) "+" else ""
        }
        h.tvTitle.text = "${tx.type.name} - ${tx.productName}"
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            .format(tx.ts.toDate())
        h.tvMeta.text = "Qty $sign${kotlin.math.abs(tx.qty)} • $dateStr"
    }
}
