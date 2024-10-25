package com.example.relogiodeponto

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.relogiodeponto.databinding.ActivitySlashinyoufaceBinding

class SlashinyoufaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlashinyoufaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySlashinyoufaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carregar o GIF usando Glide
        val gifImageView = findViewById<ImageView>(R.id.gifLogo)
        Glide.with(this)
            .load(R.drawable.logo)
            .into(gifImageView)
        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 4000)
   }
}
