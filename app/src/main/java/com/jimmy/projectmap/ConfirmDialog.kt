package com.jimmy.projectmap

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jimmy.projectmap.R

object ConfirmDialog {
    fun show(
        context: Context,
        title: String,
        message: String,
        positive: String = "Ya",
        negative: String = "Batal",
        onConfirm: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null)
        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<TextView>(R.id.tvMessage).text = message

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(positive) { d, _ -> onConfirm(); d.dismiss() }
            .setNegativeButton(negative) { d, _ -> d.dismiss() }
            .show()
    }
}
