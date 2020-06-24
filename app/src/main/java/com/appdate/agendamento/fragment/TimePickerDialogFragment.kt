package com.appdate.agendamento.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.appdate.agendamento.R
import com.appdate.agendamento.util.DateUtils
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class TimePickerDialogFragment : DialogFragment(), OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog { // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute,
                true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val inputHora: TextInputEditText = activity!!.findViewById(R.id.inputHora)
        inputHora.setText(DateUtils.timeToString(hourOfDay, minute))
        inputHora.error = null
    }
}