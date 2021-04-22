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
import com.google.firebase.firestore.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class AgendamentoFragmentList : Fragment(), OnAgendamentoSelectedListener {
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mEmptyView: LinearLayout
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAdapter: AgendamentoAdapter
    private lateinit var agendamentoRef: DocumentReference
    private lateinit var mQuery: Query
    private fun configuraSwipe() {
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
        }).attachToRecyclerView(mRecyclerView)
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
        val view = layoutInflater.inflate(R.layout.fragment_lista_gendamento, viewGroup, false)
        view.findViewById<View>(R.id.fabAdd).setOnClickListener { paramView: View? -> startActivity(Intent(activity, AddActivity::class.java)) }
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mEmptyView = view.findViewById(R.id.view_empty)
        mRecyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter
        configuraSwipe()
        return view
    }

    private fun carregarDados() {
        mQuery = mFirestore.collection("agendamentos")
                .orderBy("dataEHora", Query.Direction.ASCENDING)
        mAdapter = object : AgendamentoAdapter(mQuery, this) {
            override fun onDataChanged() { //Log.d("TAG", "Item count" + getItemCount());
                if (itemCount == 0) {
                    mEmptyView.visibility = LinearLayout.VISIBLE
                    mRecyclerView.visibility = RecyclerView.GONE
                } else {
                    mEmptyView.visibility = LinearLayout.GONE
                    mRecyclerView.visibility = RecyclerView.VISIBLE
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
        if (mAdapter != null) {
            mAdapter.startListening()
            mAdapter.notifyDataSetChanged()
        }
        // Log.d("TAG", "onResume");
    }

    override fun onPause() {
        super.onPause()
        if (mAdapter != null) {
            mAdapter.stopListening()
        }
    }
}