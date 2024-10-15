package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.relogiodeponto.databinding.ActivityMainBinding

class SelectClass : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ponto)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dbHelper = DBHelper(this)


    }
}