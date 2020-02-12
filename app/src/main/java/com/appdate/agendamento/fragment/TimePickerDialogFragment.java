package com.appdate.agendamento.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.appdate.agendamento.R;
import com.appdate.agendamento.util.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextInputEditText inputHora = getActivity().findViewById(R.id.inputHora);
        inputHora.setText(DateUtils.timeToString(hourOfDay, minute));

        inputHora.setError(null);
    }

}
