package com.jimmy.projectmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddTransactionBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    private val repo by lazy { (ServiceLocator.repo as FirebaseRepository) }

    // Arg opsional: productId & productName untuk prefill dari Detail
    private val argProductId by lazy { arguments?.getLong("productId") }
    private val argProductName by lazy { arguments?.getString("productName") }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val actProduct = view.findViewById<AutoCompleteTextView>(R.id.actProduct)
        val actType = view.findViewById<AutoCompleteTextView>(R.id.actType)
        val etQty = view.findViewById<TextInputEditText>(R.id.etQty)
        val etNote = view.findViewById<TextInputEditText>(R.id.etNote)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        // Setup dropdown produk
        val products = (ServiceLocator.repo as FirebaseRepository).products.value
        val productNames = products.map { "${it.name} (#${it.id})" }
        val adapterProd =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, productNames)
        actProduct.setAdapter(adapterProd)

        // Prefill jika dari detail
        argProductId?.let { pid ->
            val p = products.firstOrNull { it.id == pid }
            if (p != null) actProduct.setText("${p.name} (#${p.id})", false)
        } ?: run {
            argProductName?.let { actProduct.setText(it, false) }
        }

        // Setup dropdown tipe
        val types = TransactionType.values().map { it.name }
        val adapterType = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, types)
        actType.setAdapter(adapterType)
        actType.setText(types.first(), false)

        btnSave.setOnClickListener {
            val chosen = actProduct.text?.toString().orEmpty()
            val typeStr = actType.text?.toString().orEmpty()
            val qtyInt = etQty.text?.toString()?.toIntOrNull() ?: 0
            val note = etNote.text?.toString()

            if (chosen.isBlank() || qtyInt == 0 || typeStr.isBlank()) {
                Toast.makeText(requireContext(), "Produk/Qty/Tipe belum lengkap", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Parse productId dari text "Nama (#id)"
            val pid = if (argProductId != null) argProductId!!
            else Regex("#(\\d+)").find(chosen)?.groupValues?.get(1)?.toLongOrNull() ?: 0L
            val pname = chosen.substringBefore(" (#").ifBlank { argProductName ?: "" }

            val tx = StockTransaction(
                productId = pid,
                productName = pname,
                qty = qtyInt,
                type = TransactionType.valueOf(typeStr),
                note = note
            )

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    repo.addTransaction(tx)
                    Toast.makeText(requireContext(), "Transaksi tersimpan", Toast.LENGTH_SHORT).show()
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message ?: "Gagal simpan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
