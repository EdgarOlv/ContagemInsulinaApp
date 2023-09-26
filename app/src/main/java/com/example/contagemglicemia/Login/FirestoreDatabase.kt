package com.example.contagemglicemia.Login

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.databinding.FragmentFirestoreDatabaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreDatabase : Fragment() {

    private lateinit var binding: FragmentFirestoreDatabaseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirestoreDatabaseBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnInserir.setOnClickListener {
            if (binding.etCpf.text.isNotEmpty() && binding.etEmail.text.isNotEmpty() && binding.etDataNasc.text.isNotEmpty() && binding.etNome.text.isNotEmpty()) {
                val user = hashMapOf(
                    "cpf" to binding.etCpf.text.toString(),
                    "data_nasc" to binding.etDataNasc.text.toString(),
                    "email" to binding.etEmail.text.toString(),
                    "nome" to binding.etNome.text.toString()
                )

                db.collection("usuarios")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        val snackbar = Snackbar.make(requireView(), "Inserido com sucesso!! \n ID:${documentReference.id} ", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                    }
                    .addOnFailureListener { e ->
                        val snackbar = Snackbar.make(requireView(), "Erro ao inserir!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
            } else {
                Toast.makeText(requireContext(), "Insira todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConsultar.setOnClickListener {
            //actionConect()
            val docRef = db.collection("treinamentos").document("t1")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.tvResultado.setText(document.data.toString())
                        val snackbar = Snackbar.make(requireView(), "Tudo OK!! ", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                    } else {
                        val snackbar = Snackbar.make(requireView(), "Sem conteudo!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }
                .addOnFailureListener { exception ->
                    val snackbar = Snackbar.make(requireView(), "Erro ao buscar registros!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
        }

        return view
    }



    private fun actionConect() {
        val user = "edgar@teste.com"
        val key = "123456"
        if (user.isEmpty() || key.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(user, key)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val snackbar = Snackbar.make(requireView(), "Conectado com sucesso!!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)

                        val snackbar = Snackbar.make(requireView(), "Erro ao autenticar!!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }
        }
    }

    companion object {
        fun newInstance() = FirestoreDatabase()
    }
}
