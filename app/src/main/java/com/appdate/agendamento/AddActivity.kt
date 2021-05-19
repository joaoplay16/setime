package com.appdate.agendamento

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appdate.agendamento.databinding.ActivityAddBinding
import com.appdate.agendamento.fragment.DatePickerDialogFragment
import com.appdate.agendamento.fragment.TimePickerDialogFragment
import com.appdate.agendamento.model.Agendamento
import com.appdate.agendamento.util.DateUtils
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class AddActivity : AppCompatActivity(), EventListener<DocumentSnapshot?> {
    //private DatabaseReference mDatabaseReference;
    private lateinit var binding: ActivityAddBinding
    private lateinit var mFirestore: FirebaseFirestore
    private  var agendamentoId: String? = null
    private lateinit var agendamento: Agendamento
    private  var agendamentoRef: DocumentReference? = null
    private  var mAgendamentoRegistration: ListenerRegistration? = null

    companion object {
        const val KEY_AGENDAMENTO_ID = "key_agendamento_id"
        const val COLLECTION_PATH = "agendamentos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.inputData.inputType = InputType.TYPE_NULL
        binding.inputHora.inputType = InputType.TYPE_NULL
        val listener = ExibeDataListener()
        binding.inputData.setOnClickListener(listener)
        binding.inputData.onFocusChangeListener = listener
        binding.inputHora.setOnClickListener(listener)
        binding.inputHora.onFocusChangeListener = listener
        mFirestore = FirebaseFirestore.getInstance()
        agendamentoId = intent
                .getStringExtra(KEY_AGENDAMENTO_ID)
        if (agendamentoId != null) {
            agendamentoRef = mFirestore
                    .collection(COLLECTION_PATH)
                    .document(agendamentoId.toString())
            binding.btnSalvar.text = getString(R.string.edit)
        } else {
            agendamento = Agendamento(null,
                    null,null,null)
        }
    }

    fun salvar(v: View?) {
        val nome = binding.inputNome.text.toString()
        val procedimento = binding.inputProcedimento.text.toString()
        val valor = binding.inputValor.text.toString()
        val data = binding.inputData.text.toString()
        val hora = binding.inputHora.text.toString()
        if (validarCampos() == 0) {
            agendamento.nomeCliente = nome
            agendamento.procedimento = procedimento
            agendamento.valor = valor.toDouble()
            agendamento.dataEHora = DateUtils.stringToDateMilis(data, hora)
            if (agendamentoId == null) {
                pushAgendamento(agendamento)
            } else {
                updateAgendamento(agendamento)
                finish()
            }
            limparCampos()
        } else {
            Toast.makeText(this@AddActivity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateAgendamento(agendamento: Agendamento) {
        agendamentoRef?.set(agendamento)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@AddActivity,
                        agendamento.nomeCliente + "\n"
                                + DateUtils.dateMilisToString(agendamento.dataEHora) + "\n"
                                + DateUtils.timeMilisToString(agendamento.dataEHora),
                        Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AddActivity,
                        agendamento.nomeCliente + "Erro ao adicionar agendamento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun pushAgendamento(agendamento: Agendamento) {
        val agendamentoRef = mFirestore.collection(COLLECTION_PATH)
        agendamentoRef.add(agendamento).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@AddActivity,
                        agendamento.nomeCliente + "\n"
                                + DateUtils.dateMilisToString(agendamento.dataEHora) + "\n"
                                + DateUtils.timeMilisToString(agendamento.dataEHora),
                        Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AddActivity,
                        agendamento.nomeCliente + "Erro ao atualizar agendamento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEvent(documentSnapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        e?.let {
            Log.w("TAG", "agendamento:onEvent", e)
            return
        }

        agendamento = documentSnapshot?.toObject(Agendamento::class.java)!!
        onAgendamentoLoaded(agendamento)
    }



    private fun onAgendamentoLoaded(agendamento: Agendamento) {
        binding.txtTitulo.setText(R.string.editar_agendamento)
        binding.inputNome.setText(agendamento.nomeCliente)
        binding.inputProcedimento!!.setText(agendamento.procedimento)
        binding.inputValor.setText(agendamento.valor.toString())
        binding.inputData.setText(DateUtils.dateMilisToString(agendamento.dataEHora))
        binding.inputHora.setText(DateUtils.timeMilisToString(agendamento.dataEHora))
    }

    fun validarCampos(): Int {
        val nome = binding.inputNome.text.toString()
        val procedimento = binding.inputProcedimento.text.toString()
        val valor = binding.inputValor.text.toString()
        val data = binding.inputData.text.toString()
        val hora = binding.inputHora.text.toString()
        var pattern: Pattern
        var matcher: Matcher
        var erros = 0
        if (nome.trim { it <= ' ' } == "") {
            binding.inputNome.error = "Digite o nome"
            erros++
            Log.d("TAG", "Erro nome $erros")
        }
        if (procedimento.trim { it <= ' ' } == "") {
            binding.inputProcedimento.error = "Digite o procedimento"
            erros++
        }
        if (valor.trim { it <= ' ' } == "") {
            binding.inputValor.error = "Digite o valor"
            erros++
        }
        pattern = Pattern.compile("^([0-9]{2}/[0-9]{2}/[0-9]{2})$")
        matcher = pattern.matcher(data.trim { it <= ' ' })
        if (data.trim { it <= ' ' } == "") {
            binding.inputData.error = "Preencha a data"
            erros++
        } else if (!matcher.matches()) {
            erros++
            binding.inputData.error = "Data inválida"
        }
        pattern = Pattern.compile("^([0-1][0-9]|[2][0-3]):[0-5][0-9]$")
        matcher = pattern.matcher(hora.trim { it <= ' ' })
        if (hora.trim { it <= ' ' } == "") {
            binding.inputHora.error = "Preencha a hora"
            erros++
        } else if (!matcher.matches()) {
            binding.inputHora.error = "Hora inválida"
            erros++
        }
        Log.d("TAG", "Erros " + erros + " " + hora.trim { it <= ' ' })
        return erros
    }

    private fun limparCampos() {
        binding.inputNome.setText("")
        binding.inputProcedimento.setText("")
        binding.inputValor.setText("")
        binding.inputData.setText("")
        binding.inputHora.setText("")
    }

    private inner class ExibeDataListener : View.OnClickListener, OnFocusChangeListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.inputData -> showDateDialog()
                R.id.inputHora -> showTimeDialog()
            }
        }

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            when (v.id) {
                R.id.inputData -> if (hasFocus) showDateDialog()
                R.id.inputHora -> if (hasFocus) showTimeDialog()
            }
        }
    }

    private fun showDateDialog() {
        DatePickerDialogFragment().show(supportFragmentManager, "datePicker")
    }

    private fun showTimeDialog() {
        TimePickerDialogFragment().show(supportFragmentManager, "timePicker")
    }

    override fun onStart() {
        super.onStart()

        if(agendamentoRef != null) {
            mAgendamentoRegistration = agendamentoRef?.addSnapshotListener(this)

        }

    }

    override fun onStop() {
        super.onStop()

            mAgendamentoRegistration?.remove()
            //mAgendamentoRegistration = null

    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                inputManager.hideSoftInputFromWindow(Objects.requireNonNull(currentFocus)?.windowToken,
                        InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }


}