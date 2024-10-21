package com.example.relogiodeponto

import java.io.Serializable

data class Modulos(
    val id: Int = 0,
    val usuarioId: Int = 0,
    val cursoEdicaoId: Int = 0,
    val materiaId: Int = 0
) : Serializable
