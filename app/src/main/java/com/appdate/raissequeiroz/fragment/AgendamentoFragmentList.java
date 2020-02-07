package com.appdate.raissequeiroz.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appdate.raissequeiroz.AddActivity;
import com.appdate.raissequeiroz.R;
import com.appdate.raissequeiroz.adapter.AgendamentoAdapter;
import com.appdate.raissequeiroz.model.Agendamento;
import com.appdate.raissequeiroz.util.LibraryClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class AgendamentoFragmentList extends Fragment{
  private static final String AGENDAMENTOS_CHILD = "agendamentos";
  private FirebaseRecyclerAdapter<Agendamento, AgendamentoAdapter.AgendamentoViewHolder> mAdapter;
  private DatabaseReference mFirebaseDatabaseReference;
  private DatabaseReference agendamentosRef;
  private LinearLayoutManager mLayoutManager;
  private RecyclerView mRecyclerView;
  private ProgressBar progressBar;


  private void configuraSwipe() {
    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 12) {
          public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder target) {


            return false;
          }
          
          public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            viewHolder.setIsRecyclable(true);
            //direction = viewHolder.getLayoutPosition();

            try {
              final Agendamento a  =mAdapter.getItem(viewHolder.getAdapterPosition());
              agendamentosRef.child(a.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()){
                    Toast.makeText(getActivity(), "Agendaento de " + a.getNomeCliente() + " removido", Toast.LENGTH_SHORT).show();
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
    mFirebaseDatabaseReference = LibraryClass.getFirebase();
    carregarDados("dataEHora");
  }

  @Nullable
  public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
    View view = layoutInflater.inflate(R.layout.fragment_lista_gendamento, viewGroup, false);
    progressBar = view.findViewById(R.id.progressBar);
    view.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
          public void onClick(View param1View) {
            startActivity(new Intent(getActivity(), AddActivity.class));
          }
        });
      this.mRecyclerView = view.findViewById(R.id.recyclerView);
      this.mRecyclerView.setHasFixedSize(true);

      mLayoutManager = new LinearLayoutManager(getActivity());
      mLayoutManager.setReverseLayout(true);
      mLayoutManager.setStackFromEnd(true);

    this.mRecyclerView.setLayoutManager(this.mLayoutManager);

    this.mRecyclerView.setAdapter(this.mAdapter);
    configuraSwipe();
    return view;
  }

  public void carregarDados(String sortOrder){
    SnapshotParser<Agendamento> parser = new SnapshotParser<Agendamento>() {
      @NonNull
      @Override
      public Agendamento parseSnapshot(@NonNull DataSnapshot snapshot) {
        Agendamento agendamento = snapshot.getValue(Agendamento.class);
        if(agendamento != null){
          agendamento.setId(snapshot.getKey());
          progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
        return agendamento;
      }
    };

    agendamentosRef = mFirebaseDatabaseReference.child(AGENDAMENTOS_CHILD);

    /*Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR, 23);
    calendar.set(Calendar.MINUTE, 59);*/

    FirebaseRecyclerOptions<Agendamento> options =
            new FirebaseRecyclerOptions.Builder<Agendamento>()
                    .setQuery(agendamentosRef.orderByChild(sortOrder), parser)
                    .build();

    this.mAdapter = new AgendamentoAdapter(options);
  }

  @Override
  public void onResume() {
    super.onResume();
    mAdapter.startListening();
  }

  @Override
  public void onPause() {
    super.onPause();
    mAdapter.stopListening();
  }

}


