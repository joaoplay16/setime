package com.appdate.agendamento.fragment

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdate.agendamento.AddActivity
import com.appdate.agendamento.R
import com.appdate.agendamento.adapter.AgendamentoAdapter
import com.appdate.agendamento.adapter.AgendamentoAdapter.AgendamentoViewHolder
import com.appdate.agendamento.adapter.AgendamentoAdapter.OnAgendamentoSelectedListener
import com.appdate.agendamento.adapter.GeneralItem
import com.appdate.agendamento.databinding.FragmentListaGendamentoBinding
import com.google.firebase.firestore.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class AgendamentoFragmentList : Fragment(), OnAgendamentoSelectedListener {
    private var _binding: FragmentListaGendamentoBinding? = null
    private val binding get() = _binding!!
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAdapter: AgendamentoAdapter
    private lateinit var agendamentoRef: DocumentReference
    private lateinit var mQuery: Query
    private fun setupSwipe() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, 12) {
            override fun onMove(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.setIsRecyclable(true)
                //direction = viewHolder.getLayoutPosition();
                if (viewHolder is AgendamentoViewHolder) {
                    try {
                        val generalItem = mAdapter.getItem(viewHolder.getAdapterPosition()) as GeneralItem
                        val snapshot = generalItem.documentSnapshot
                        agendamentoRef = mFirestore.collection("agendamentos").document(snapshot!!.id)
                        agendamentoRef.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) { // Toast.makeText(getActivity(), "Agendamento de " + snapshot.getString("nomeCliente") + " excluído", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return
                    } catch (nullPointerException: NullPointerException) {
                        nullPointerException.printStackTrace()
                        return
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                                     viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (viewHolder is AgendamentoViewHolder) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(activity!!, R.color.swipe_delete))
                            .addActionIcon(android.R.drawable.ic_menu_delete)
                            .create()
                            .decorate()
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        mFirestore = FirebaseFirestore.getInstance()
        mFirestore.firestoreSettings = settings
        carregarDados()
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListaGendamentoBinding.inflate(layoutInflater, viewGroup, false)
        val view = binding.root
        setupSwipe()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fabAdd.setOnClickListener { paramView: View? -> startActivity(Intent(activity, AddActivity::class.java)) }
        binding.recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = mLayoutManager
        binding.recyclerView.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun carregarDados() {
        mQuery = mFirestore.collection("agendamentos")
                .orderBy("dataEHora", Query.Direction.ASCENDING)
        mAdapter = object : AgendamentoAdapter(mQuery, this) {
            override fun onDataChanged() { //Log.d("TAG", "Item count" + getItemCount());
                if (itemCount == 0) {
                    binding.viewEmpty.visibility = LinearLayout.VISIBLE
                    binding.recyclerView.visibility = RecyclerView.GONE
                } else {
                   binding.viewEmpty.visibility = LinearLayout.GONE
                    binding.recyclerView.visibility = RecyclerView.VISIBLE
                }
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onAgendamentoSelected(snapshot: DocumentSnapshot?) {
        val intent = Intent(activity, AddActivity::class.java)
        intent.putExtra(AddActivity.KEY_AGENDAMENTO_ID, snapshot?.id)
        startActivity(intent)
    }



    override fun onResume() {
        super.onResume()
        mAdapter.startListening()
        mAdapter.notifyDataSetChanged()
        // Log.d("TAG", "onResume");
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
    }
}