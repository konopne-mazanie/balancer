package com.uhreckysw.balancer.backend;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidStr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateCommon {
    public static final SimpleDateFormat dateFormatGUI = new SimpleDateFormat("dd.MM.yyyy");

    public static void pickDate(Date defaultDate, final LambdaVoidStr callback, Activity context) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(defaultDate);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.MyDatePicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        callback.fun(dayOfMonth + "." + (monthOfYear + 1) + "." + year);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public static Date parseDateGUI(String s) {
        String[] formatStrings = new String[]{"dd.MM.yyyy", "dd/MM/yyyy"};
        for (String formatString : formatStrings)
        {
            try
            {
                return new SimpleDateFormat(formatString).parse(s);
            }
            catch (ParseException e) {}
        }

        return null;
    }
}
