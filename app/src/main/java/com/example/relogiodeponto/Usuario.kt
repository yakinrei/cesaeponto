package com.example.relogiodeponto

import java.io.Serializable

data class Usuario(
    val id: Int = 0,
    val cargoId: Int = 0,
    val email: String = "",
    val nome: String = "",
    val senha: String = "",
    val contato: String = "",
    val sms: Boolean = false
) : Serializable