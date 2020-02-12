package com.appdate.agendamento;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appdate.agendamento.fragment.DatePickerDialogFragment;
import com.appdate.agendamento.fragment.TimePickerDialogFragment;
import com.appdate.agendamento.model.Agendamento;
import com.appdate.agendamento.util.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    public static final String KEY_RESTAURANT_ID = "key_agendamento_id";
    public static final String COLLECTION_PATH = "agendamentos";

    //private DatabaseReference mDatabaseReference;
    private FirebaseFirestore mFirestore;

    private Button botaoSalvar;
    private TextView txtTitulo;
    private TextInputEditText inputNome, inputProcedimento, inputValor , inputData, inputHora;
    private String agendamentoId;
    private Agendamento agendamento;
    private DocumentReference agendamentoRef;
    private ListenerRegistration mAgendamentoRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        botaoSalvar = findViewById(R.id.btnSalvar);
        inputNome = findViewById(R.id.inputNome);
        inputProcedimento = findViewById(R.id.inputProcedimento);
        inputValor = findViewById(R.id.inputValor);
        inputData = findViewById(R.id.inputData);
        inputHora = findViewById(R.id.inputHora);
        txtTitulo = findViewById(R.id.txtTitulo);

        inputData.setInputType(InputType.TYPE_NULL);
        inputHora.setInputType(InputType.TYPE_NULL);

        ExibeDataListener listener = new ExibeDataListener();
        inputData.setOnClickListener(listener);
        inputData.setOnFocusChangeListener(listener);

        inputHora.setOnClickListener(listener);
        inputHora.setOnFocusChangeListener(listener);

        mFirestore = FirebaseFirestore.getInstance();

        agendamentoId = getIntent()
                .getStringExtra(KEY_RESTAURANT_ID);


           if(agendamentoId != null){
               agendamentoRef = mFirestore
                       .collection("agendamentos")
                       .document(agendamentoId);
               botaoSalvar.setText("Editar");
           }else {
               agendamento = new Agendamento();
           }
    }

    public void salvar(View v){
        String nome = inputNome.getText().toString();
        String procedimento = inputProcedimento.getText().toString();
        String valor = inputValor.getText().toString();
        String data = inputData.getText().toString();
        String hora = inputHora.getText().toString();


        if (validarCampos() == 0){
            agendamento.setNomeCliente(nome);
            agendamento.setProcedimento(procedimento);
            agendamento.setValor(Double.parseDouble(valor));
            agendamento.setDataEHora(DateUtils.stringToDateMilis(data, hora));

            if (agendamentoId == null){

                pushAgendamento(agendamento);
            }else {
                updateAgendamento(agendamento);
                finish();
            }

            limparCampos();
        }else {
            Toast.makeText(AddActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();

        }
    }

    public void updateAgendamento(final Agendamento agendamento){
        agendamentoRef.set(agendamento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddActivity.this,
                            agendamento.getNomeCliente() + "\n"
                                    + DateUtils.dateMilisToString(agendamento.getDataEHora()) + "\n"
                                    + DateUtils.timeMilisToString(agendamento.getDataEHora()),
                                      Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddActivity.this,
                            agendamento.getNomeCliente() + "Erro ao adicionar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void pushAgendamento(final Agendamento agendamento){
        CollectionReference agendamentoRef = mFirestore.collection("agendamentos");
        agendamentoRef.add(agendamento).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddActivity.this,
                            agendamento.getNomeCliente() + "\n"
                                    + DateUtils.dateMilisToString(agendamento.getDataEHora()) + "\n"
                                    + DateUtils.timeMilisToString(agendamento.getDataEHora()),
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddActivity.this,
                            agendamento.getNomeCliente() + "Erro ao atualizar agendamento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("TAG", "agendamento:onEvent", e);
            return;
        }
        agendamento = documentSnapshot.toObject(Agendamento.class);
        onAgendamentoLoaded(agendamento);
    }

    private void onAgendamentoLoaded(Agendamento agendamento){
        txtTitulo.setText(R.string.editar_agendamento);
        inputNome.setText(agendamento.getNomeCliente());
        inputProcedimento.setText(agendamento.getProcedimento());
        inputValor.setText(String.valueOf(agendamento.getValor()));
        inputData.setText(DateUtils.dateMilisToString(agendamento.getDataEHora()));
        inputHora.setText(DateUtils.timeMilisToString(agendamento.getDataEHora()));
    }

    public int validarCampos(){
        String nome = inputNome.getText().toString();
        String procedimento = inputProcedimento.getText().toString();
        String valor = inputValor.getText().toString();
        String data = inputData.getText().toString();
        String hora = inputHora.getText().toString();

        Pattern pattern;
        Matcher matcher;

        int erros = 0;

        if (nome.trim().equals("")) {
            inputNome.setError("Digite o nome");
            erros++;
            Log.d("TAG", "Erro nome " + erros);
        }
        if (procedimento.trim().equals("")){
            inputProcedimento.setError("Digite o procedimento");
            erros++;
        }
        if (valor.trim().equals("")){
            inputValor.setError("Digite o valor");
            erros++;
        }

        pattern = Pattern.compile("^([0-9]{2}\\/[0-9]{2}\\/[0-9]{2})$");
        matcher = pattern.matcher(data.trim());
        if (data.trim().equals("")){

            inputData.setError("Preencha a data");
            erros++;
        }else if(!matcher.matches()){
            erros++;
            inputData.setError("Data inválida");
        }
            pattern = Pattern.compile("^([0-1][0-9]|[2][0-3]):[0-5][0-9]$");
            matcher = pattern.matcher(hora.trim());
        if (hora.trim().equals("")){
            inputHora.setError("Preencha a hora");
            erros++;
        }else if(!matcher.matches()){
            inputHora.setError("Hora inválida");
            erros++;
        }

        Log.d("TAG", "Erros "  + erros + " " + hora.trim());

        return erros;
    }

    private void limparCampos(){
        inputNome.setText("");
        inputProcedimento.setText("");
        inputValor.setText("");
        inputData.setText("");
        inputHora.setText("");
    }

    private class ExibeDataListener implements View.OnClickListener, View.OnFocusChangeListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.inputData:
                    showDateDialog();
                    break;
                case R.id.inputHora:
                    showTimeDialog();
                    break;
            }
        }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            switch (v.getId()){
                case R.id.inputData:
                    if (hasFocus)
                        showDateDialog();
                    break;
                case R.id.inputHora:
                    if (hasFocus)
                        showTimeDialog();
                    break;
            }

        }
    }

    private void showDateDialog(){
        new DatePickerDialogFragment().show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimeDialog(){
        new TimePickerDialogFragment().show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (agendamentoRef != null)
            mAgendamentoRegistration = agendamentoRef.addSnapshotListener(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAgendamentoRegistration != null) {
            mAgendamentoRegistration.remove();
            mAgendamentoRegistration = null;
        }
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }
    }
}
