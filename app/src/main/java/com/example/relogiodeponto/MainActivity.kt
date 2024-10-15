package com.example.relogiodeponto

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.relogiodeponto.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Connect to the database as soon as the activity starts
        lifecycleScope.launch {
            connectToDatabase()
        }

        // Button event listeners
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    loginUsuario(username, password)
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
                // Check if the database is connected
                database.getReference(".info/connected").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == true) {
                            showToast("Conectado ao banco de dados com sucesso!")
                        } else {
                            showToast("Erro ao conectar ao banco de dados.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Erro ao conectar ao banco de dados: ${error.message}")
                    }
                })
                true // Connection successful
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Erro ao conectar ao banco de dados: ${e.message}")
                false
            }
        }

        withContext(Dispatchers.Main) {
            if (connectionResult) {
                showToast("Conectado ao banco de dados com sucesso!")
            } else {
                showToast("Erro ao conectar ao banco de dados.")
            }
        }
    }

    // Function to handle user login
    private suspend fun loginUsuario(email: String, password: String) {
        val usersRef = database.getReference("users")

        withContext(Dispatchers.IO) {
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(email)) {
                        val user = snapshot.child(email).getValue(User::class.java)
                        if (user != null && user.password == password) {
                            withContext(Dispatchers.Main) {
                                showToast("Login realizado com sucesso! Tipo: ${user.type}")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                showToast("Usuário ou senha incorretos.")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showToast("Usuário não encontrado.")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    withContext(Dispatchers.Main) {
                        showToast("Erro ao verificar usuário: ${error.message}")
                    }
                }
            })
        }
    }

    // Function to recover the user password
    private suspend fun recuperarSenha(email: String) {
        val usersRef = database.getReference("users")

        withContext(Dispatchers.IO) {
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(email)) {
                        val user = snapshot.child(email).getValue(User::class.java)
                        if (user != null) {
                            withContext(Dispatchers.Main) {
                                showToast("Sua senha é: ${user.password}")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                showToast("Erro ao recuperar a senha. Tente novamente.")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showToast("Email não encontrado.")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    withContext(Dispatchers.Main) {
                        showToast("Erro ao recuperar a senha: ${error.message}")
                    }
                }
            })
        }
    }
}