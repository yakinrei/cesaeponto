package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.relogiodeponto.databinding.ActivityAdministradorBinding
import com.example.relogiodeponto.databinding.ActivityProfessorBinding

class AdministradorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdministradorBinding
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdministradorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.getSerializableExtra("Usuario") as Usuario
        binding.textView2.text = "Olá, ${usuario.nome}!"
    }

}