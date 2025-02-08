package com.example.contagemglicemia.dao

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class FirebaseDB {

    private lateinit var dbManager: MyDatabaseManager

    fun InserirEmNuvem(
        context: Context,
        glicemia: Glicemia,
    ) {
        try {
            val timeZoneBahia = TimeZone.getTimeZone("America/Bahia")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            dateFormat.timeZone = timeZoneBahia

            val date = Date()
            val dateString = dateFormat.format(date)

            val glicemiaCloud = glicemia.toGlicemiaCloud()
            val database = Firebase.database
            val instanceCloud = database.getReference("glicemia")

            val data = glicemiaCloud.data

            instanceCloud.child(dateString.toString()).setValue(glicemiaCloud)
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

        glicemiaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("HardwareIds")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val glicemias: MutableList<GlicemiaClean> = mutableListOf()
                val existingDates = dbManager.getAllGlicemyDates()
                val currentDeviceId = Build.MODEL
                //Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                for (snapshot in dataSnapshot.children) {
                    try {
                        val glicemia = snapshot.getValue(GlicemiaClean::class.java)
                        if (glicemia != null) {
                            val data = glicemia.data

                            if (glicemia.loc != currentDeviceId && !existingDates.contains(data)) {
                                dbManager.insertGlycemia(glicemia.toGlicemia())
                                glicemias.add(glicemia)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ReceberListNuvem", "Erro ao processar dados do snapshot", e)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(
                    "ReceberListNuvem",
                    "Erro na consulta do Firebase",
                    databaseError.toException()
                )
            }
        })
    }


}
