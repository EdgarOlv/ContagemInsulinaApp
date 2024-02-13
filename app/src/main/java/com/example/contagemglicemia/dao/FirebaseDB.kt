package com.example.contagemglicemia.dao

import android.content.Context
import android.widget.Toast
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.model.GlicemiaClean
import com.example.contagemglicemia.model.dateFormat
import com.example.contagemglicemia.model.toGlicemia
import com.example.contagemglicemia.model.toGlicemiaCloud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseDB {

    private lateinit var dbManager: MyDatabaseManager

    fun InserirEmNuvem(
        context: Context,
        glicemia: Glicemia,
    ) {
        try {
            val glicemiaCloud = glicemia.toGlicemiaCloud()
            val database = Firebase.database
            val instanceCloud = database.getReference("glicemia")

            instanceCloud.child(glicemiaCloud.data.toString()).setValue(glicemiaCloud)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao inserir na nuvem", Toast.LENGTH_SHORT).show()
        }
    }

    fun InserirListaEmNuvem(
        context: Context,
        list: List<Glicemia>,
    ) {
        try {
            for (itemGlicemy in list) {
                val glicemiaCloud = itemGlicemy.toGlicemiaCloud()
                val database = Firebase.database
                val instanceCloud = database.getReference("glicemia")

                instanceCloud.child(glicemiaCloud.data.toString()).setValue(glicemiaCloud)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao inserir na nuvem", Toast.LENGTH_SHORT).show()
        }
    }

    fun ReceberListNuvem(context: Context) {
        dbManager = MyDatabaseManager(context)
        val database = FirebaseDatabase.getInstance()
        val glicemiaRef = database.getReference("glicemia")

        // val query = glicemiaRef.orderByChild("loc").equalTo("pc")
        val query = glicemiaRef.orderByChild("loc")
            .startAt("pc")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val glicemias: MutableList<GlicemiaClean> = mutableListOf()
                val existingDates = dbManager.getAllGlicemyDates()
                var lastGlicemia = GlicemiaClean()

                for (snapshot in dataSnapshot.children) {
                    try {
                        val glicemia = snapshot.getValue(GlicemiaClean::class.java)
                        if (glicemia != null && lastGlicemia != glicemia) {
                            val data = glicemia.data
                            val dateConverted = dateFormat.parse(data)

                            if (!existingDates.contains(dateConverted)) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    dbManager.insertGlycemia(glicemia.toGlicemia())
                                }
                                lastGlicemia = glicemia
                                glicemias.add(glicemia)
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
