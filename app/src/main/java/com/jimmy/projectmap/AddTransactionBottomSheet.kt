package com.jimmy.projectmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddTransactionBottomSheet : com.google.android.material.bottomsheet.BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val etProduct = view.findViewById<TextInputEditText>(R.id.etProduct)
        val etQty = view.findViewById<TextInputEditText>(R.id.etQty)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        btnSave.setOnClickListener {
            // TODO: validasi & kirim data pakai callback/SharedViewModel
            dismiss()
        }
    }
}
