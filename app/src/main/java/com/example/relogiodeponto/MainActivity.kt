package com.example.relogiodeponto

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


        //   val usersRef = database.getReference("Usuarios")
//
//// Ler todos os usuários do Realtime Database
//        usersRef.get().addOnSuccessListener { dataSnapshot ->
//            dataSnapshot.children.forEach { snapshot ->
//                val usuario = snapshot.getValue(Usuario::class.java)
//                usuario?.let {
//                    // Para cada usuário, criar uma conta no Firebase Authentication
//                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(it.email, "senhaPadrao123")
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                // Usuário criado com sucesso
//                                Log.d("Migration", "Usuário ${it.nome} migrado com sucesso!")
//                            } else {
//                                // Erro ao criar usuário no Firebase Authentication
//                                Log.e("Migration", "Erro ao migrar usuário ${it.nome}: ${task.exception?.message}")
//                            }
//                        }
//                }
//            }
//        }.addOnFailureListener { e ->
//            Log.e("Migration", "Erro ao buscar usuários do banco de dados: ${e.message}")
//        }


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
            val email = binding.usernameInput.text.toString()
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    recuperarSenha(email)
                }
            } else {
                showToast("Por favor, insira seu email.")
            }
        }
    }

    private suspend fun recuperarSenha(email: String) {
        try {
            // Ao fazer o login, obtemos o usuário
            val user = getUserFromEmail(email)

            // Processamento de redirecionamento na Main Thread
            withContext(Dispatchers.Main) {
                if (user != null) {
                    // Redirecionar com base em cargoId
                    enviarEmailDeRecuperacao(user.email)
                } else {
                    // Informar ao usuário sobre credenciais incorretas
                    showToast("Usuário não encontrado.")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                showToast("Erro ao buscar usuário: ${e.message}")
            }
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



//    private suspend fun realizarLogin(email: String, password: String) {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Verifica se o usuário existe no Realtime Database
//                val resultadoLogin = loginUsuario(email, password)
//
//                // Mova a exibição do Toast para a thread principal
//                withContext(Dispatchers.Main) {
////                    showToast(resultadoLogin) // Exibir resultados do login
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    showToast("Erro ao realizar login: ${e.message}")
//                }
//            }
//        }
//    }
//
// //Essa Funciona
//    private suspend fun loginUsuario(email: String, password: String): String {
//        val usersRef = database.getReference("Usuarios")
//        return suspendCoroutine { continuation ->
//            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val user = snapshot.children.first().getValue(Usuario::class.java)
//                        // Verifica a senha do usuário
//                        if (user != null && user.senha == password) {
//                            continuation.resume("Login realizado com sucesso! Tipo: ${user.cargoId}")
//                        } else {
//                            continuation.resume("Usuário ou senha incorretos.")
//                        }
//                    } else {
//                        continuation.resume("Usuário não encontrado.")
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    continuation.resume("Erro ao acessar o banco de dados: ${error.message}")
//                }
//            })
//        }
//    }



    private suspend fun realizarLogin(email: String, password: String) {
        try {
            // Ao fazer o login, obtemos o usuário
            val user = getUserFromEmail(email)

            // Processamento de redirecionamento na Main Thread
            withContext(Dispatchers.Main) {
                if (user != null && user.senha == password) {
                    // Redirecionar com base em cargoId
                    redirecionarPorCargo(user)
                } else {
                    // Informar ao usuário sobre credenciais incorretas
                    showToast("Usuário ou senha incorretos.")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e(TAG, "realizarLogin: ${e.message}")
                showToast("Erro ao realizar login: ${e.message}")
            }
        }
    }

    private suspend fun getUserFromEmail(email: String): Usuario? {
        val usersRef = database.getReference("Usuarios")
        return suspendCancellableCoroutine { continuation ->
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!continuation.isCompleted) {
                        val user = snapshot.children.firstOrNull()?.getValue(Usuario::class.java)
                        if (user != null) {
                            continuation.resume(user)
                        } else {
                            continuation.resume(null)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(Exception("Erro ao acessar o banco de dados: ${error.message}"))
                    }
                }
            })
        }
    }

    private fun redirecionarPorCargo(user: Usuario) {
        val intent = when (user.cargoId) {
            1 -> Intent(this, AdministradorActivity::class.java)
            2 -> Intent(this, ProfessorActivity::class.java)
            3 -> Intent(this, AlunoActivity::class.java)
            else -> Intent(this, MainActivity::class.java) // Caso haja erro no cargoId
        }
        intent.putExtra("Usuario", user as java.io.Serializable)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
       Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}

