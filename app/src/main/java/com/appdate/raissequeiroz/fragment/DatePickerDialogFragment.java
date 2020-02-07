package com.appdate.raissequeiroz.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.appdate.raissequeiroz.AddActivity;
import com.appdate.raissequeiroz.R;
import com.appdate.raissequeiroz.util.DateUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int ano =  calendar.get(Calendar.YEAR);
        int mes =  calendar.get(Calendar.MONTH);
        int dia =  calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, ano, mes, dia);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        view.setMinDate(Calendar.getInstance().getTimeInMillis());

        TextInputEditText inputData = getActivity().findViewById(R.id.inputData);
        inputData.setText(DateUtils.dateToString(year, month, dayOfMonth));

        inputData.setError(null);
    }

}
