package com.fshangala.y11mlambo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.DialogFragment

class OddsDialogFragment: DialogFragment() {
    internal lateinit var listener: OddsDialogListener
    interface OddsDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment,oddsData: OddsData)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.odds,null)

            builder.setView(dialogView)
                .setPositiveButton("Set",
            DialogInterface.OnClickListener { dialog, id ->
                // Send the positive button event back to the host activity
                val team = dialogView.findViewById<Spinner>(R.id.teamSpinner)
                val backlay = dialogView.findViewById<Spinner>(R.id.backlaySpinner)
                val odds = dialogView.findViewById<EditText>(R.id.oddsInput)
                val stake = dialogView.findViewById<EditText>(R.id.stakeInput)

                val oddsData = OddsData(
                    team.selectedItem.toString(),
                    backlay.selectedItem.toString(),
                    odds.text.toString().toDouble(),
                    stake.text.toString().toDouble()
                )
                listener.onDialogPositiveClick(this,oddsData)
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