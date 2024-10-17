package com.example.relogiodeponto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.relogiodeponto.databinding.ActivityEsqueciamerdadaminhasenhaBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

class EsqueciamerdadaminhasenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEsqueciamerdadaminhasenhaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEsqueciamerdadaminhasenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Recuperando o objeto 'Usuario' da intent
        usuario = intent.getSerializableExtra("Usuario") as? Usuario
            ?: run {
                showToast("Erro ao carregar usuário.")
                finish()
                return
            }
        enviarEmailDeRecuperacao(usuario.email)

        Log.d("EsqueciMinhaSenhaActivity", "Usuario recebido: ${usuario.nome}, Email: ${usuario.email}")
        binding.emailTextView.text = "Email: ${usuario.email}"

        binding.reenviarCodigoButton.setOnClickListener {
            enviarEmailDeRecuperacao(usuario.email)
        }

        binding.retornar.setOnClickListener {
            redirecionarPorCargo(null)
        }

        binding.alterarSenhaButton.setOnClickListener {
            val novaSenha = binding.novaSenhaEditText.text.toString()
            val confirmarSenha = binding.confirmarSenhaEditText.text.toString()

            if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                showToast("Por favor, preencha todos os campos.")
                return@setOnClickListener
            }

            if (novaSenha != confirmarSenha) {
                showToast("As senhas não correspondem.")
                return@setOnClickListener
            }

            alterarSenha(usuario.email, novaSenha)
        }
    }

    private fun enviarEmailDeRecuperacao(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Email de redefinição de senha enviado para $email.")
                } else {
                    showToast("Erro ao enviar o email de redefinição. Tente novamente.")
                    Log.e("EsqueciMinhaSenhaActivity", "Erro: ${task.exception?.message}")
                }
            }
    }

    private fun alterarSenha(email: String, novaSenha: String) {
        // O Firebase já gerencia a troca de senha através do link enviado por email.
        showToast("Siga as instruções no email para alterar sua senha.")
    }

    private fun redirecionarPorCargo(user: Usuario?) {
        val intent = when (user?.cargoId) {
            1 -> Intent(this, AdministradorActivity::class.java)
            2 -> Intent(this, ProfessorActivity::class.java)
            3 -> Intent(this, AlunoActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        intent.putExtra("Usuario", user as Serializable)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
