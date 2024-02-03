package com.example.contagemglicemia.modules.Firebase

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.contagemglicemia.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    var textResponse: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        textResponse = binding.TextviewUser

        binding.buttonConnectar.setOnClickListener {
            actionConect()
        }
        binding.buttonDesc.setOnClickListener {
            actionDisconect()
        }

        IdentificaUser()

        return binding.root
    }

    private fun IdentificaUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.TextviewUser.setText("Ativo: ${currentUser.email}")
        }
    }

    private fun actionCadastro() {
        val user = binding.editTextUsername.text.toString()
        val key = binding.editTextPassword.text.toString()
        if (user.isEmpty() || key.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(user, key)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val snackbar = Snackbar.make(
                            requireView(),
                            "Inserido com sucesso!!",
                            Snackbar.LENGTH_SHORT,
                        )
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i(TAG, "createUserWithEmail:failure", task.exception)
                        val snackbar =
                            Snackbar.make(requireView(), "Erro ao inserir!!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }
        }
    }

    private fun actionConect() {
        val user = binding.editTextUsername.text.toString()
        val key = binding.editTextPassword.text.toString()
        if (user.isEmpty() || key.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(user, key)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        val snackbar = Snackbar.make(
                            requireView(),
                            "Conectado com sucesso!!",
                            Snackbar.LENGTH_SHORT,
                        )
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                        IdentificaUser()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)

                        val snackbar = Snackbar.make(
                            requireView(),
                            "Erro ao autenticar!!",
                            Snackbar.LENGTH_SHORT,
                        )
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                }
        }
    }

    private fun actionDisconect() {
        auth.signOut()
        textResponse?.setText("Disconetado")
    }

    companion object {

        fun newInstance() = LoginFragment()
    }
}
