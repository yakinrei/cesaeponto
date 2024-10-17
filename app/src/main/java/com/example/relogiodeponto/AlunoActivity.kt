package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.relogiodeponto.databinding.ActivityAlunoBinding
import com.example.relogiodeponto.databinding.ActivityProfessorBinding

class AlunoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlunoBinding
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.getSerializableExtra("Usuario") as Usuario
        binding.textView3.text = "Olá, ${usuario.nome}!"
    }

}