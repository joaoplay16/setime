package com.appdate.agendamento.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdate.agendamento.AddActivity;
import com.appdate.agendamento.R;
import com.appdate.agendamento.adapter.AgendamentoAdapter;
import com.appdate.agendamento.model.Agendamento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class AgendamentoFragmentList extends Fragment implements
        AgendamentoAdapter.OnAgendamentoSelectedListener {

  private LinearLayoutManager mLayoutManager;
  private RecyclerView mRecyclerView;
  private LinearLayout mEmptyView;
  private FirebaseFirestore mFirestore;
  private AgendamentoAdapter mAdapter;
  private DocumentReference agendamentoRef;
  private Query mQuery;


  private void configuraSwipe() {
    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 12) {
      public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder target) {

        return false;
      }

      public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        viewHolder.setIsRecyclable(true);
        //direction = viewHolder.getLayoutPosition();

        try {

          DocumentSnapshot snapshot = mAdapter.getItem(viewHolder.getAdapterPosition());
          agendamentoRef = mFirestore.collection("agendamentos").document(snapshot.getId());
          Agendamento a = snapshot.toObject(Agendamento.class);

          agendamentoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                //Toast.makeText(getActivity(), "Agendamento de " + a.getNomeCliente() + " excluído", Toast.LENGTH_SHORT).show();
              }
            }
          });

          return;
        } catch (NullPointerException nullPointerException) {
          nullPointerException.printStackTrace();
          return;
        }
      }

      @Override
      public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              float dX, float dY, int actionState, boolean isCurrentlyActive) {

        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.swipe_delete))
                .addActionIcon(android.R.drawable.ic_menu_delete)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

      }
    }).attachToRecyclerView(this.mRecyclerView);
  }


  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
    mFirestore = FirebaseFirestore.getInstance();
    mFirestore.setFirestoreSettings(settings);
    carregarDados();


  }

  @Nullable
  public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
    View view = layoutInflater.inflate(R.layout.fragment_lista_gendamento, viewGroup, false);
    view.findViewById(R.id.fabAdd).setOnClickListener(paramView ->
            startActivity(new Intent(getActivity(), AddActivity.class)));

    this.mRecyclerView = view.findViewById(R.id.recyclerView);
    this.mEmptyView = view.findViewById(R.id.view_empty);
    this.mRecyclerView.setHasFixedSize(true);

    mLayoutManager = new LinearLayoutManager(getActivity());


    this.mRecyclerView.setLayoutManager(this.mLayoutManager);

    this.mRecyclerView.setAdapter(this.mAdapter);

    configuraSwipe();


    return view;
  }


  private void carregarDados(){
    mQuery = mFirestore.collection("agendamentos")
            .orderBy("dataEHora", Query.Direction.ASCENDING);
    mAdapter = new AgendamentoAdapter(mQuery, this){
      @Override
      protected void onDataChanged() {
        Log.d("TAG", "Item count" + getItemCount());

        if(getItemCount() == 0){
          mEmptyView.setVisibility(ListView.VISIBLE);
          mRecyclerView.setVisibility(RecyclerView.GONE);
        }else {
          mEmptyView.setVisibility(ListView.GONE);
          mRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();

      }
    };
  }

  @Override
  public void onAgendamentoSelected(DocumentSnapshot snapshot) {
    Intent intent = new Intent(getActivity(), AddActivity.class);
    intent.putExtra(AddActivity.KEY_RESTAURANT_ID, snapshot.getId());

    startActivity(intent);
  }

  @Override
  public void onStart() {
    super.onStart();
    //Log.d("TAG", "OnStart");

  }


  @Override
  public void onResume() {
    super.onResume();
    if (mAdapter != null){
      mAdapter.startListening();
      mAdapter.notifyDataSetChanged();
    }
    // Log.d("TAG", "onResume");

  }

  @Override
  public void onPause() {
    super.onPause();
    if (mAdapter != null) {
      mAdapter.stopListening();
    }
  }

}


