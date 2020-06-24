package com.appdate.agendamento.model

import java.io.Serializable

 class Agendamento : Serializable{

    var nomeCliente: String? = null
    var procedimento: String? = null
    var valor: Double?  = null
    var dataEHora: Long? =  null

     constructor()

     constructor(nomeCliente: String?, procedimento: String?, valor: Double?, dataEHora: Long?) {
         this.nomeCliente = nomeCliente
         this.procedimento = procedimento
         this.valor = valor
         this.dataEHora = dataEHora
     }

 }

