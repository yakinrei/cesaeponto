package com.example.relogiodeponto

import java.io.Serializable

data class CursoEdicao(
    val id: Int = 0,
    val ListaMateriasId: Int = 0,
    val edicao: Int = 0,
    val poloId: Int = 0,
    val dataInicio: String = "",
    val pagamentohora: Int = 0,
    val pagamentotransporte: Int = 0,
    val pagamentokm: Int = 0,
    val pagamentorefeicao: Int = 0
) : Serializable
