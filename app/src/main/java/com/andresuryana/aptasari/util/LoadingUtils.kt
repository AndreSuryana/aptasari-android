package com.andresuryana.aptasari.util

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.andresuryana.aptasari.R

object LoadingUtils {

    class LoadingDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val loadingDialog = AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_loading)
                .setCancelable(false)
                .create()

            // Prevent the dialog from being canceled on touch outside
            loadingDialog.setCanceledOnTouchOutside(false)

            return loadingDialog
        }
    }

    fun FragmentActivity.showLoadingDialog() {
        val loadingDialog = LoadingDialogFragment()

        // Show the loading dialog using the FragmentManager
        val fragmentManager = supportFragmentManager
        val tag = "loading_dialog"
        val existingDialog = fragmentManager.findFragmentByTag(tag)

        // If the dialog is already shown, don't show another instance
        if (existingDialog == null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(loadingDialog, tag)
            fragmentTransaction.commitAllowingStateLoss()
        }
    }

    fun FragmentActivity.dismissLoadingDialog() {
        val fragmentManager = supportFragmentManager
        val loadingDialog =
            fragmentManager.findFragmentByTag("loading_dialog") as? LoadingDialogFragment
        loadingDialog?.dismiss()
    }
}