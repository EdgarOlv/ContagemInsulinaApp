package com.example.contagemglicemia.dao

import android.content.Context
import android.widget.Toast
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.model.GlicemiaClean
import com.example.contagemglicemia.model.toGlicemia
import com.example.contagemglicemia.model.toGlicemiaCloud
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

            instanceCloud.child(glicemiaCloud.data).setValue(glicemiaCloud)
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao inserir na nuvem", Toast.LENGTH_SHORT).show()
        }
    }

    fun ReceberListNuvem(context: Context) {
        dbManager = MyDatabaseManager(context)
        val database = FirebaseDatabase.getInstance()
        val glicemiaRef = database.getReference("glicemia")

        val query = glicemiaRef.orderByChild("loc").equalTo("pc")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val glicemias: MutableList<GlicemiaClean> = mutableListOf()
                val existingDates = dbManager.getAllGlicemyDates()

                for (snapshot in dataSnapshot.children) {
                    val glicemia = snapshot.getValue(GlicemiaClean::class.java)
                    if (glicemia != null) {
                        val data = glicemia.data

                        if (!existingDates.contains(data)) {
                            dbManager.insertGlycemia(glicemia.toGlicemia())
                        }

                        glicemias.add(glicemia)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
