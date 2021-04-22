package com.appdate.agendamento.adapter

import com.google.firebase.firestore.DocumentSnapshot

class GeneralItem : ListItem() {
    var documentSnapshot: DocumentSnapshot? = null

    override fun getType(): Int {
        return TYPE_GENERAL
    }
}