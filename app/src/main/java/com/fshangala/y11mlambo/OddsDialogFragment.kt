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
        fun openBet(dialog: DialogFragment, oddsData: OddsData)
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

                var oddsvalue = 0.0
                if (odds.text.toString() != "") {
                    oddsvalue = odds.text.toString().toDouble()
                }

                var stakevalue = 0.0
                if (stake.text.toString() != ""){
                    stakevalue = stake.text.toString().toDouble()
                }

                val oddsData = OddsData(
                    team.selectedItem.toString(),
                    backlay.selectedItem.toString(),
                    oddsvalue,
                    stakevalue
                )
                listener.onDialogPositiveClick(this,oddsData)
            })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    // Send the negative button event back to the host activity
                    listener.onDialogNegativeClick(this)
                })
                .setNeutralButton("Open bet",
                DialogInterface.OnClickListener{dialog,id ->
                    val team = dialogView.findViewById<Spinner>(R.id.teamSpinner)
                    val backlay = dialogView.findViewById<Spinner>(R.id.backlaySpinner)
                    val odds = dialogView.findViewById<EditText>(R.id.oddsInput)
                    val stake = dialogView.findViewById<EditText>(R.id.stakeInput)

                    var oddsvalue = 0.0
                    if (odds.text.toString() != "") {
                        oddsvalue = odds.text.toString().toDouble()
                    }

                    var stakevalue = 0.0
                    if (stake.text.toString() != ""){
                        stakevalue = stake.text.toString().toDouble()
                    }

                    val oddsData = OddsData(
                        team.selectedItem.toString(),
                        backlay.selectedItem.toString(),
                        oddsvalue,
                        stakevalue
                    )
                    listener.openBet(this,oddsData)
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