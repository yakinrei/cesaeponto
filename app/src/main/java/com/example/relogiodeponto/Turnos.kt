package com.example.relogiodeponto

import java.io.Serializable

data class Turnos(
    val id: Int = 0,
    val periodo: String = "",
    val inicia: String = "",
    val termina: String = ""
) : Serializable
