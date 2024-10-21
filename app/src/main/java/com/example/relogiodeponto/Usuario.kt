package com.example.relogiodeponto

import java.io.Serializable

data class Usuario(
    val Id: Int = 0,
    val cargoId: Int = 0,
    val email: String = "",
    val nome: String = "",
    val senha: String = "",
    val porto: Int = 0,
    val braga: Int = 0,
    val online: Int = 0,
    val CC: String = "",
    val sms: Int = 0,
    val contato: Int = 0
) : Serializable