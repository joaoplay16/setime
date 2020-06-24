package com.appdate.agendamento.adapter

class DateItem : ListItem() {
    var date: String? = null

    override fun getType(): Int {
        return TYPE_DATE
    }
}