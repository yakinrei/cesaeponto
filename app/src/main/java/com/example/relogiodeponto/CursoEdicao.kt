package com.example.relogiodeponto

import java.io.Serializable

data class CursoEdicao(
    val id: Int = 0,
    val ListaMateriasId: Int = 0,
    val edicao: Int = 0,
    val poloId: Int = 0,
    val dataInicio: String = "",
    val hora: Float = 0.0f,
    val transporte: Float = 0.0f,
    val km: Float = 0.0f,
    val refeicao: Float = 0.0f,
    val nome: String = ""
) : Serializable
