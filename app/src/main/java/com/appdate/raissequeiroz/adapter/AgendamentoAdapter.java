package com.appdate.raissequeiroz.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.appdate.raissequeiroz.AddActivity;
import com.appdate.raissequeiroz.R;
import com.appdate.raissequeiroz.model.Agendamento;
import com.appdate.raissequeiroz.util.DateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Calendar;

public class AgendamentoAdapter extends FirebaseRecyclerAdapter<Agendamento, AgendamentoAdapter.AgendamentoViewHolder> {

private Context mContext;

    public AgendamentoAdapter(FirebaseRecyclerOptions<Agendamento> options) {
        super(options);
    }

    @NonNull
    @Override
    public AgendamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AgendamentoViewHolder(inflater.inflate(R.layout.card_agendamento, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull AgendamentoViewHolder holder, int position, final Agendamento agendamento) {
        holder.setIsRecyclable(false);
        holder.txtNome.setText(agendamento.getNomeCliente());
        holder.txtProcedimento.setText(agendamento.getProcedimento());
        holder.txtValor.setText(mContext.getString(R.string.adp_valor, agendamento.getValor()));

        String diaDaSemana = DateUtils.diaDaSemana(mContext, agendamento.getDataEHora());
        String data = DateUtils.dateMilisToString(agendamento.getDataEHora());
        String dataAtual = DateUtils.dateMilisToString(Calendar.getInstance().getTimeInMillis());
        if(data.trim().equals(dataAtual.trim())){
            diaDaSemana = "Hoje";
            holder.txtDiaDaSemana.setAllCaps(true);
            holder.txtDiaDaSemana.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

            Log.d("TAG", data + " | " + dataAtual);

        }
        holder.txtDiaDaSemana.setText(diaDaSemana);
        holder.txtData.setText(data);
        holder.txtHora.setText(DateUtils.timeMilisToString(agendamento.getDataEHora()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", agendamento.getNomeCliente() + " / " + agendamento.getId());
                mContext.startActivity(new Intent(mContext, AddActivity.class).putExtra("agendamento", agendamento));
            }
        });


    }

    public static class AgendamentoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        TextView txtProcedimento;
        TextView txtValor;
        TextView txtData;
        TextView txtHora;
        TextView txtDiaDaSemana;

        public AgendamentoViewHolder(View v) {
            super(v);
            txtDiaDaSemana = itemView.findViewById(R.id.txtDiaDaSemana);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtProcedimento = itemView.findViewById(R.id.txtProcedimento);
            txtValor = itemView.findViewById(R.id.txtValor);
            txtData = itemView.findViewById(R.id.txtData);
            txtHora = itemView.findViewById(R.id.txtHora);
        }
    }


}
