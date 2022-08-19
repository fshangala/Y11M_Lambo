package com.fshangala.y11mlambo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class OddsDialogFragment: DialogFragment() {
    internal lateinit var listener: OddsDialogListener
    interface OddsDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Hello world")
                .setPositiveButton("Set",
            DialogInterface.OnClickListener { dialog, id ->
                // Send the positive button event back to the host activity
                listener.onDialogPositiveClick(this)
            })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    // Send the negative button event back to the host activity
                    listener.onDialogNegativeClick(this)
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OddsDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement OddsDialogListener"))
        }
    }
}