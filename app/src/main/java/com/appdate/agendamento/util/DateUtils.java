package com.appdate.agendamento.util;


import android.content.Context;


import com.appdate.agendamento.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {


    public static String dateToString(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

        return simpleDateFormat.format(calendar.getTime());
    }

    public static String timeToString(int hora, int min){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR, hora);
//        calendar.set(Calendar.MINUTE, min);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//
//        return simpleDateFormat.format(calendar.getTime());
        String h = String.valueOf(hora),
                m = String.valueOf(min);
        if (hora < 10){
            h = "0" + h;
        }

        if (min < 10){
            m = "0" + min;
        }

        return h + ":" + m;
    }

    public static String timeMilisToString(long dataEHora){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dataEHora);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String time = simpleDateFormat.format(calendar.getTime());
        //Log.d("TAG", time);

//        int h = calendar.get(Calendar.HOUR_OF_DAY);
//        int m = calendar.get(Calendar.MINUTE);
//        String hora, min;
//        hora = String.valueOf(h);
//        min = String.valueOf(m);
//        if(h < 10){
//            hora = "0" + hora;
//        }
//
//        if(m < 10){
//            min = "0" + min;
//        }
//
//        return hora+":"+min;

        return time;
    }

    public static String dateMilisToString(long data){
//        Calendar calendar = Calendar.getInstance();
//        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
//        calendar.setTimeInMillis(data);
//        String dt = format.format(calendar.getTime());

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yy");

        return simpleDateFormat.format(data);
    }

    public static Long stringToDateMilis(String data, String hora){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy-HH:mm");
        try {
            cal.setTime(simpleDateFormat.parse(data + "-" + hora));
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*String[] dt = data.split("/");
        int dia = Integer.parseInt(dt[0]);
        int mes = Integer.parseInt(dt[1]) -1;
        int ano = Integer.parseInt(dt[2]);

        String[] h = hora.split(":");
        int hr = Integer.parseInt(h[0]);
        int min = Integer.parseInt(h[1]);

        Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia, hr, min);

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        String d = format.format(c.getTime());
        Log.d("TAG", ""+mes);*/

        return 0l;
    }


    public static String diaDaSemana(Context context, Long date){
        Calendar calendar = Calendar.getInstance();
        Date d = new Date(date);
        calendar.setTime(d);

        String[] dias = context.getResources().getStringArray(R.array.semana);

        int dds = calendar.get(Calendar.DAY_OF_WEEK);
        String diaDaSemana = dias[dds-1];

        return diaDaSemana;
    }
}
