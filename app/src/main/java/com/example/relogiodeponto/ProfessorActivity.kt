package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.relogiodeponto.databinding.ActivityProfessorBinding

class  ProfessorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorBinding
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.getSerializableExtra("Usuario") as Usuario
        binding.textView.text = "Olá, ${usuario.nome}!"
    }

}