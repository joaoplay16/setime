package com.appdate.agendamento.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appdate.agendamento.R;
import com.appdate.agendamento.model.Agendamento;
import com.appdate.agendamento.util.DateUtils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class AgendamentoAdapter extends FirestoreAdapter<AgendamentoAdapter.AgendamentoViewHolder> {


    public interface OnAgendamentoSelectedListener {
        void onAgendamentoSelected(DocumentSnapshot restaurant);
    }

    private OnAgendamentoSelectedListener mListener;

    public AgendamentoAdapter(Query query, OnAgendamentoSelectedListener listener) {
        super(query);
        this.mListener = listener;

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public AgendamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.card_agendamento, parent, false);

        return new AgendamentoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendamentoViewHolder holder, int position) {
       holder.setIsRecyclable(false);

       holder.bind(getSnapshot(position), mListener);

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

        private void bind(final DocumentSnapshot snapshot,
                          final OnAgendamentoSelectedListener listener){
            Agendamento agendamento = snapshot.toObject(Agendamento.class);

            txtNome.setText(agendamento.getNomeCliente());
            txtProcedimento.setText(agendamento.getProcedimento());
            txtValor.setText(itemView.getContext().getString(R.string.adp_valor, agendamento.getValor()));

            String diaDaSemana = DateUtils.diaDaSemana(itemView.getContext(), agendamento.getDataEHora());
            String data = DateUtils.dateMilisToString(agendamento.getDataEHora());
            String dataAtual = DateUtils.dateMilisToString(Calendar.getInstance().getTimeInMillis());
                if(data.trim().equals(dataAtual.trim())){
                    diaDaSemana = "Hoje";
                    txtDiaDaSemana.setAllCaps(true);
                    txtDiaDaSemana.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent));

                }
            Log.d("TAG", "");

            txtDiaDaSemana.setText(diaDaSemana);
            txtData.setText(data);
            txtHora.setText(DateUtils.timeMilisToString(agendamento.getDataEHora()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onAgendamentoSelected(snapshot);
                    }
                }
            });

            }
    }


}
