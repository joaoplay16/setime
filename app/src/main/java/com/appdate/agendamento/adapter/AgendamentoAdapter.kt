package com.appdate.agendamento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appdate.agendamento.R
import com.appdate.agendamento.model.Agendamento
import com.appdate.agendamento.util.DateUtils.dataAtualInMilis
import com.appdate.agendamento.util.DateUtils.dataAtualToString
import com.appdate.agendamento.util.DateUtils.dateMilisToString
import com.appdate.agendamento.util.DateUtils.diaDaSemana
import com.appdate.agendamento.util.DateUtils.stringToDateMilis
import com.appdate.agendamento.util.DateUtils.timeMilisToString
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

open class AgendamentoAdapter(mQuery: Query?, private var mListener:OnAgendamentoSelectedListener) :
        FirestoreAdapter<RecyclerView.ViewHolder>(mQuery) {

    interface OnAgendamentoSelectedListener {
        fun onAgendamentoSelected(snapshot: DocumentSnapshot?)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v: View
        return when (viewType) {
            ListItem.TYPE_GENERAL -> {
                v = inflater.inflate(R.layout.card_agendamento, parent, false)
                AgendamentoViewHolder(v)
            }
            ListItem.TYPE_DATE -> {
                v = inflater.inflate(R.layout.separador_agendamento, parent, false)
                DataViewHolder(v)
            }
            else -> null!!
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (holder is AgendamentoViewHolder) {
            val generalItem = getItem(position) as GeneralItem
            val snapshot:DocumentSnapshot? = generalItem.documentSnapshot
            holder.bind(snapshot, mListener)
        } else if (holder is DataViewHolder) {
            val dateItem = getItem(position) as DateItem
            holder.bind(dateItem)
        }
    }

    class DataViewHolder internal constructor(v: View?) : RecyclerView.ViewHolder(v!!) {
        var txtData: TextView
        var txtDiaDaSemana: TextView
        fun bind(dateItem: DateItem) {
            txtData.text = dateItem.date
            val dataDoSnapshotInMilis = stringToDateMilis(dateItem.date, null)
            var strDiaDaSemana = diaDaSemana(itemView.context, dataDoSnapshotInMilis)
            val dataAtual = dataAtualToString
            val dataDoSnapshot = dateMilisToString(dataDoSnapshotInMilis)
            val diaMilis = 86400000
            val dataDiaSeguinte = dateMilisToString(dataAtualInMilis + diaMilis)
            if (dateItem.date?.trim { it <= ' ' } == dataAtual.trim { it <= ' ' }) {
                strDiaDaSemana = "Hoje"
                txtDiaDaSemana.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
                txtData.visibility = View.INVISIBLE
            } else if (dataDoSnapshot.trim { it <= ' ' } == dataDiaSeguinte.trim { it <= ' ' }) {
                strDiaDaSemana = "Amanhã"
                txtDiaDaSemana.setTextColor(ContextCompat.getColor(itemView.context, R.color.corSeparador))
                txtData.visibility = View.INVISIBLE
            }
            txtDiaDaSemana.text = strDiaDaSemana
        }

        init {
            txtData = itemView.findViewById(R.id.txtData)
            txtDiaDaSemana = itemView.findViewById(R.id.txtDiaDaSemana)
        }
    }

    class AgendamentoViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        var txtNome: TextView
        var txtProcedimento: TextView
        var txtValor: TextView
        var txtHora: TextView
        fun bind(snapshot: DocumentSnapshot?,
                 listener: OnAgendamentoSelectedListener?) {
            val agendamento = snapshot?.toObject(Agendamento::class.java)
            txtNome.text = agendamento?.nomeCliente
            txtProcedimento.text = agendamento?.procedimento
            txtValor.text = itemView.context.getString(R.string.adp_valor, agendamento?.valor)
            txtHora.text = timeMilisToString(agendamento?.dataEHora)
            itemView.setOnClickListener { listener?.onAgendamentoSelected(snapshot) }
        }

        init {
            txtNome = itemView.findViewById(R.id.txtNome)
            txtProcedimento = itemView.findViewById(R.id.txtProcedimento)
            txtValor = itemView.findViewById(R.id.txtValor)
            txtHora = itemView.findViewById(R.id.txtHora)
        }
    }

}