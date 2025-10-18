package com.jimmy.projectmap.core.util

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/* -----------------------------
   TOAST (Activity & Fragment)
-------------------------------- */

fun Activity.toast(message: CharSequence, isLong: Boolean = false) {
    Toast.makeText(this, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Activity.toast(@StringRes messageRes: Int, isLong: Boolean = false) {
    Toast.makeText(this, messageRes, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: CharSequence, isLong: Boolean = false) {
    requireActivity().toast(message, isLong)
}

fun Fragment.toast(@StringRes messageRes: Int, isLong: Boolean = false) {
    requireActivity().toast(messageRes, isLong)
}

/* -----------------------------
   SNACKBAR (View, Fragment)
   - with optional action
-------------------------------- */

fun View.snack(
    message: CharSequence,
    isLong: Boolean = false,
    actionText: CharSequence? = null,
    action: (() -> Unit)? = null
) {
    val sb = Snackbar.make(this, message, if (isLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
    if (actionText != null && action != null) {
        sb.setAction(actionText) { action() }
    }
    sb.show()
}

fun View.snack(@StringRes messageRes: Int, isLong: Boolean = false) {
    Snackbar.make(this, messageRes, if (isLong) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).show()
}

/**
 * Convenience untuk Fragment – otomatis cari root view yang aman (viewLifecycleOwner).
 * Fallback ke requireView() kalau rootView null.
 */
fun Fragment.snack(
    message: CharSequence,
    isLong: Boolean = false,
    actionText: CharSequence? = null,
    action: (() -> Unit)? = null
) {
    val root = view ?: requireActivity().findViewById(android.R.id.content)
    root.snack(message, isLong, actionText, action)
}

fun Fragment.snack(@StringRes messageRes: Int, isLong: Boolean = false) {
    val root = view ?: requireActivity().findViewById(android.R.id.content)
    root.snack(messageRes, isLong)
}

/* -----------------------------------------
   Helper cepat untuk status: success/error
   (secara default Snackbar warna mengikuti theme;
    kalau mau warna custom, bisa override via theme)
------------------------------------------ */

fun Fragment.snackSuccess(message: CharSequence) = snack(message, isLong = false)
fun Fragment.snackError(message: CharSequence) = snack(message, isLong = true)

