package com.appdate.agendamento.fragment

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.appdate.agendamento.R
import com.appdate.agendamento.util.DateUtils
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class DatePickerDialogFragment : DialogFragment(), OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val ano = calendar[Calendar.YEAR]
        val mes = calendar[Calendar.MONTH]
        val dia = calendar[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(activity!!, this, ano, mes, dia)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val inputData: TextInputEditText = activity!!.findViewById(R.id.inputData)
        inputData.setText(DateUtils.dateToString(year, month, dayOfMonth))
        inputData.error = null
    }
}