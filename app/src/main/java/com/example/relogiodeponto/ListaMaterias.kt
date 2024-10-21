package com.example.relogiodeponto

import java.io.Serializable

data class ListaMaterias(
    val id: Int = 0,
    val nome: String = "",
    val list: List<Materias> = emptyList()
) : Serializable

