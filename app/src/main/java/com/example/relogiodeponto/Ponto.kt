package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.relogiodeponto.databinding.ActivityMainBinding
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Ponto : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ponto)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dbHelper = DBHelper(this)
        val numeroCartao = lerCartaoInteligente()
//        registrarPonto(numeroCartao)

    }


    // Função para registrar o ponto
//    fun registrarPonto(numeroCartao: String) {
////        val conn = conectarBanco()
//        val queryAluno = "SELECT id FROM Alunos WHERE numero_cartao = ?"
//        val queryRegistro = "INSERT INTO RegistrosPonto (id_aluno, data_hora) VALUES (?, ?)"
//
//        try {
//            // Verifica se o cartão pertence a um aluno
//            val pstmtAluno: PreparedStatement = conn.prepareStatement(queryAluno)
//            pstmtAluno.setString(1, numeroCartao)
//            val rs: ResultSet = pstmtAluno.executeQuery()
//
//            if (rs.next()) {
//                val idAluno = rs.getInt("id")
//                val dataHoraAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//
//                // Insere o registro de ponto
//                val pstmtRegistro: PreparedStatement = conn.prepareStatement(queryRegistro)
//                pstmtRegistro.setInt(1, idAluno)
//                pstmtRegistro.setString(2, dataHoraAtual)
//                pstmtRegistro.executeUpdate()
//
//                println("Ponto registrado para o aluno de ID: $idAluno em $dataHoraAtual")
//            } else {
//                println("Cartão não registrado.")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            conn.close()
//        }
//    }

    // Simulação da leitura de um cartão inteligente
    fun lerCartaoInteligente(): String {
        // Aqui você teria a integração com o hardware do leitor de cartão
        // Para simulação, retornaremos um número de cartão fixo
        return "123456789"  // Número fictício do cartão do cidadão
    }





}