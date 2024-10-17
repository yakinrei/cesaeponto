package com.example.relogiodeponto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.relogiodeponto.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import java.io.Serializable


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Database e FirebaseAuth
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Button event listeners
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    realizarLogin(username, password)
                }
            } else {
                showToast("Preencha todos os campos.")
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            if (username.isNotEmpty()) {
                lifecycleScope.launch {
                    recuperarSenha(username)
                }
            } else {
                showToast("Informe o nome de usuário.")
            }
        }
    }

    private suspend fun connectToDatabase() {
        val connectionResult = withContext(Dispatchers.IO) {
            try {
                suspendCancellableCoroutine<Boolean> { continuation ->
                    database.getReference(".info/connected").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            continuation.resume(snapshot.value == true) // Retorna o estado da conexão
                        }

                        override fun onCancelled(error: DatabaseError) {
                            showToast("Erro ao conectar ao banco de dados: ${error.message}")
                            continuation.resumeWithException(Exception("Erro ao conectar ao banco de dados: ${error.message}"))
                        }
                    })
                }
            } catch (e: Exception) {
                showToast("Erro ao conectar ao banco de dados: ${e.message}")
                e.printStackTrace()
                throw Exception("Erro ao conectar ao banco de dados: ${e.message}")
            }
        }

        // Exibe a mensagem após obter o resultado da conexão
        showToast(if (connectionResult) "Conectado ao banco de dados com sucesso!" else "Erro ao conectar ao banco de dados.")
    }

    private suspend fun realizarLogin(email: String, password: String) {
        return withContext(Dispatchers.IO) {
            try {
                // Verifica se o usuário existe no Realtime Database
                val resultadoLogin = loginUsuario(email, password)

                // Mova a exibição do Toast para a thread principal
                withContext(Dispatchers.Main) {
                    showToast(resultadoLogin) // Exibir resultados do login
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Erro ao realizar login: ${e.message}")
                }
            }
        }
    }

 //Essa Funciona
    private suspend fun loginUsuario(email: String, password: String): String {
        val usersRef = database.getReference("Usuarios")
        return suspendCoroutine { continuation ->
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(Usuario::class.java)
                        // Verifica a senha do usuário
                        if (user != null && user.senha == password) {
                            continuation.resume("Login realizado com sucesso! Tipo: ${user.cargoId}")
                        } else {
                            continuation.resume("Usuário ou senha incorretos.")
                        }
                    } else {
                        continuation.resume("Usuário não encontrado.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume("Erro ao acessar o banco de dados: ${error.message}")
                }
            })
        }
    }


    private fun recuperarSenha(email: String) {
        lifecycleScope.launch {
            val user = recuperarSenhaUsuario(email)
            if (user != null) {
                // Se o usuário for encontrado, redireciona para a nova Activity com o usuário
                val intent = Intent(this@MainActivity, EsqueciamerdadaminhasenhaActivity::class.java)
                intent.putExtra("usuario", user) // Passa o objeto 'Usuario' para a nova Activity
                startActivity(intent)
            } else {
                showToast("Erro ao recuperar a senha. Tente novamente.")
            }
        }
    }

    private suspend fun recuperarSenhaUsuario(email: String): Usuario? {
        val usersRef = database.getReference("Usuarios")
        return suspendCoroutine { continuation ->
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(Usuario::class.java)
                        if (user != null) {
                            continuation.resume(user) // Retorna o objeto Usuario
                        } else {
                            continuation.resume(null)
                        }
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }
            })
        }
    }

    private fun showToast(message: String) {
       Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}

private fun Intent.putExtra(key: String, user: Usuario): Intent {
    this.putExtra(key, user as Serializable)
    return this
}
