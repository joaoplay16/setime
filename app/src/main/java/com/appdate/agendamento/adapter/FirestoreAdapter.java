/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.appdate.agendamento.adapter;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 * 
 * See the adapter classes in FirebaseUI (https://github.com/firebase/FirebaseUI-Android/tree/master/firestore) for a
 * more efficient implementation of a Firestore RecyclerView Adapter.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements EventListener<QuerySnapshot> {

    private static final String TAG = "Firestore Adapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private ArrayList<DocumentSnapshot> mSnapshots = new ArrayList<>();

    public FirestoreAdapter(Query query) {
        mQuery = query;
    }

    public void startListening() {
        if(mQuery != null && mRegistration == null){
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public DocumentSnapshot getItem(int position) {
        return mSnapshots.get(position);
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index);
    }

    protected void onError(FirebaseFirestoreException e) {}

    protected void onDataChanged() {
    }

    protected void onDocumentAdded(DocumentChange change){
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change){
        // Item alterado, mas permaneceu na mesma posição
        if(change.getOldIndex() == change.getNewIndex()){
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        }else {
            // Item alterado e posição alterada
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex(), change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change){
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

        //lida com erros
        if(e !=null){
            Log.w(TAG, "onEvent:error ", e);
            return;
        }

        //Despacha o evento
        for (DocumentChange change: queryDocumentSnapshots.getDocumentChanges()){
            //Snapshot do documento alterado
            DocumentSnapshot snapshot = change.getDocument();

            switch (change.getType()){
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }


        onDataChanged();
    }
}
