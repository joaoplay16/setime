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
package com.appdate.agendamento.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.appdate.agendamento.util.DateUtils.dateMilisToString
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*

/**
 * RecyclerView adapter for displaying the results of a Firestore [Query].
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * [DocumentSnapshot.toObject] is not cached so the same object may be deserialized
 * many times as the user scrolls.
 *
 * See the adapter classes in FirebaseUI (https://github.com/firebase/FirebaseUI-Android/tree/master/firestore) for a
 * more efficient implementation of a Firestore RecyclerView Adapter.
 */
abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder?>(private var mQuery: Query?) : RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot?> {
    private var mRegistration: ListenerRegistration? = null
    private val mConsolidatedList: MutableList<ListItem> = ArrayList()
    private val mSnapshots = ArrayList<DocumentSnapshot>()
    fun startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration?.remove()
            mRegistration = null
        }
        mSnapshots.clear()
        mConsolidatedList.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query?) { // Stop listening
        stopListening()
        // Clear existing data
        mSnapshots.clear()
        notifyDataSetChanged()
        // Listen to new query
        mQuery = query
        startListening()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return mConsolidatedList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mConsolidatedList[position].getType()
    }

    fun getItem(index: Int): ListItem {
        return mConsolidatedList[index]
    }

    protected fun onError(e: FirebaseFirestoreException?) {}
    protected open fun onDataChanged() {}
    protected fun onDocumentAdded(change: DocumentChange) {
        mSnapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    protected fun onDocumentModified(change: DocumentChange) { // Item alterado, mas permaneceu na mesma posição
        if (change.oldIndex == change.newIndex) {
            mSnapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else { // Item alterado e posição alterada
            mSnapshots.removeAt(change.oldIndex)
            mSnapshots.add(change.newIndex, change.document)
            notifyItemChanged(change.oldIndex, change.newIndex)
        }
    }

    protected fun onDocumentRemoved(change: DocumentChange) {
        mSnapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    override fun onEvent(queryDocumentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException?) { //lida com erros
        if (e != null) {
            //Log.w(TAG, "onEvent:error ", e)
            return
        }
        //Despacha o evento
        for (change in queryDocumentSnapshots!!.documentChanges) { //Snapshot do documento alterado
            val snapshot: DocumentSnapshot = change.document
            when (change.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(change)
                DocumentChange.Type.MODIFIED -> onDocumentModified(change)
                DocumentChange.Type.REMOVED -> onDocumentRemoved(change)
            }
        }
        groupByDate(mSnapshots)
        onDataChanged()
    }

    fun groupByDate(snapshotList: List<DocumentSnapshot>) {
        mConsolidatedList.clear()
        if (snapshotList.size > 0) {
            val groupedHashMap = groupDataIntoHashMap(snapshotList)
            for (date in groupedHashMap.keys) {
                val dateItem = DateItem()
                dateItem.date = date
                mConsolidatedList.add(dateItem)
                for (snapshot in groupedHashMap[date]!!) {
                    val generalItem = GeneralItem()
                    generalItem.documentSnapshot = snapshot
                    mConsolidatedList.add(generalItem)
                }
            }
        }
    }

    private fun groupDataIntoHashMap(listOfDoumentSnapshot: List<DocumentSnapshot>): TreeMap<String, MutableList<DocumentSnapshot>?> {
        val groupedHashMap = TreeMap<String, MutableList<DocumentSnapshot>?>()
        for (snapshot in listOfDoumentSnapshot) {
            val hashMapKey = dateMilisToString(snapshot.getLong("dataEHora")!!)
            Log.d("TAGY", "hashMapKey: $hashMapKey")
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(snapshot)
            } else {
                val list: MutableList<DocumentSnapshot> = ArrayList()
                list.add(snapshot)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }



}