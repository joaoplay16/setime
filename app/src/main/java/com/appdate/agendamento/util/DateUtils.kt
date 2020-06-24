package com.appdate.agendamento.util

import android.content.Context
import com.appdate.agendamento.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun dateToString(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar[year, monthOfYear] = dayOfMonth
        val simpleDateFormat = SimpleDateFormat("dd/MM/yy")
        return simpleDateFormat.format(calendar.time)
    }

    fun timeToString(hora: Int, min: Int): String { //        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR, hora);
//        calendar.set(Calendar.MINUTE, min);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//
//        return simpleDateFormat.format(calendar.getTime());
        var h = hora.toString()
        var m = min.toString()
        if (hora < 10) {
            h = "0$h"
        }
        if (min < 10) {
            m = "0$min"
        }
        return "$h:$m"
    }

    @JvmStatic
    fun timeMilisToString(dataEHora: Long?): String {
        val calendar = Calendar.getInstance()
        dataEHora?.let {
            calendar.timeInMillis = dataEHora
        }
        val simpleDateFormat = SimpleDateFormat("HH:mm")
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
        return simpleDateFormat.format(calendar.time)
    }

    @JvmStatic
    fun dateMilisToString(data: Long?): String { //        Calendar calendar = Calendar.getInstance();
//        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
//        calendar.setTimeInMillis(data);
//        String dt = format.format(calendar.getTime());
        val simpleDateFormat = SimpleDateFormat("dd/MM/yy")
        return simpleDateFormat.format(data)
    }

    @JvmStatic
    fun stringToDateMilis(data: String?, hora: String?): Long {
        val cal = Calendar.getInstance()
        try {
            if (hora == null) {
                val simpleDateFormat = SimpleDateFormat("dd/MM/yy")
                cal.time = simpleDateFormat.parse(data)
            } else {
                val simpleDateFormat = SimpleDateFormat("dd/MM/yy-HH:mm")
                cal.time = simpleDateFormat.parse("$data-$hora")
            }
            return cal.timeInMillis
        } catch (e: ParseException) {
            e.printStackTrace()
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
        Log.d("TAG", ""+mes);*/return 0L
    }

    @JvmStatic
    fun diaDaSemana(context: Context, date: Long?): String {
        val calendar = Calendar.getInstance()
        val d = Date(date!!)
        calendar.time = d
        val dias = context.resources.getStringArray(R.array.semana)
        val dds = calendar[Calendar.DAY_OF_WEEK]
        return dias[dds - 1]
    }

    @JvmStatic
    val dataAtualInMilis: Long
        get() = Calendar.getInstance().timeInMillis

    @JvmStatic
    val dataAtualToString: String
        get() {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yy")
            return simpleDateFormat.format(dataAtualInMilis)
        }
}