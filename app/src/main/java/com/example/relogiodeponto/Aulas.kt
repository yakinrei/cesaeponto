package com.example.relogiodeponto

import java.io.Serializable

data class Aulas(
    val id: Int = 0,
    val moduloId: Int = 0,
    val diaprevisto: String = "",
    val diarealizado: String = "",
    val turno: Int = 0
) : Serializable
