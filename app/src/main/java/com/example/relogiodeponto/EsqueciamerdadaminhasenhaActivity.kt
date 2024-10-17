package com.example.relogiodeponto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.os.CountDownTimer
import android.widget.Toast
import com.example.relogiodeponto.databinding.ActivityEsqueciamerdadaminhasenhaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EsqueciamerdadaminhasenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEsqueciamerdadaminhasenhaBinding
    private lateinit var codigoEnviado: String
    private lateinit var usuario: Usuario
    private var podeReenviarCodigo = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEsqueciamerdadaminhasenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Recebe o objeto Usuario que foi passado pela Intent
        usuario = intent.getSerializableExtra("Usuario") as Usuario

        // Exibe o e-mail do usuário
        binding.emailTextView.text = "Email: ${usuario.email}"

        // Campos de código e senha
        val codigo = binding.codigoEditText.text.toString()
        val novaSenha = binding.novaSenhaEditText.text.toString()
        val confirmarSenha = binding.confirmarSenhaEditText.text.toString()
        val reenviarCodigo = binding.reenviarCodigoButton
        val alterarSenha = binding.alterarSenhaButton
        val retornar = binding.retornar

        // Gera e envia o código quando a activity é criada
        codigoEnviado = gerarCodigoVerificacao()
        EmailService.enviarEmail(usuario.email, "Seu código de verificação", "Seu código é: $codigoEnviado")
        startReenvioTimer()

        // Reenvio de código com limitação de 3 minutos
        reenviarCodigo.setOnClickListener {
            if (podeReenviarCodigo) {
                codigoEnviado = gerarCodigoVerificacao()
                EmailService.enviarEmail(usuario.email, "Seu código de verificação", "Seu código é: $codigoEnviado")
                startReenvioTimer()
            } else {
                showToast("Você pode reenviar o código em até 3 minutos.")
            }
        }

        retornar.setOnClickListener {
            redirecionarPorCargo(null)
        }

        // Lógica para alterar a senha
        alterarSenha.setOnClickListener {
            if (codigo != codigoEnviado) {
                showToast("Código incorreto!")
            } else if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                showToast("Por favor, preencha todos os campos.")
            } else if (novaSenha != confirmarSenha) {
                showToast("As senhas não correspondem.")
            } else {
                alterarSenha(usuario, novaSenha)
            }
        }
    }


        // Função para iniciar o timer de 5 minutos para reenviar código
        private fun startReenvioTimer() {
            podeReenviarCodigo = false
            object : CountDownTimer(180000, 1000) { // 3 minutos = 180000 ms
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    podeReenviarCodigo = true
                }
            }.start()
        }

        // Função para alterar a senha do usuário no Firebase
        private fun alterarSenha(usuario: Usuario, novaSenha: String) {
            val usersRef = FirebaseDatabase.getInstance().getReference("Usuarios")
            usersRef.child(usuario.email).child("senha").setValue(novaSenha).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Senha alterada com sucesso!")
                    redirecionarPorCargo(usuario)
                } else {
                    showToast("Erro ao alterar a senha. Tente novamente.")
                }
            }
        }

        // Função para redirecionar de acordo com o cargo do usuário
        private fun redirecionarPorCargo(user: Usuario?) {
            val intent = when (user?.cargoId) {
                1 -> Intent(this, AdministradorActivity::class.java)
                2 -> Intent(this, ProfessorActivity::class.java)
                3 -> Intent(this, AlunoActivity::class.java)
                else -> Intent(this, MainActivity::class.java) // Caso algo dê errado
            }
            intent.putExtra("Usuario", user as Serializable) // Passa email para a nova Activity
            startActivity(intent)
            finish()
        }

        private fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }


    // Função para gerar um código aleatório de 6 dígitos
    fun gerarCodigoVerificacao(): String {
        return Random.nextInt(100000, 999999).toString()
    }
}