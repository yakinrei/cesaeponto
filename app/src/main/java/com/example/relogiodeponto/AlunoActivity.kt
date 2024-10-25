package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import com.github.mikephil.charting.charts.BarChart
import com.example.relogiodeponto.databinding.ActivityAlunoBinding
import com.example.relogiodeponto.databinding.ActivityProfessorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AlunoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlunoBinding
    private lateinit var database: FirebaseDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializa o Firebase Database e FirebaseAuth
        database = FirebaseDatabase.getInstance()
        // Criar instâncias da classe Atrassos
        val atraso1 = Atrassos() // present será false
        val atraso2 = Atrassos(true) // present será true
        val atraso3 = Atrassos(present = false, faltas = 2) // present será false, faltas será 2
    }

    private suspend fun getAssiduidadeByUser(userId: String): List<Atrassos> {
        val assiduidadeRef = database.getReference("Assiduidade").child(userId)
        return suspendCancellableCoroutine { continuation ->
            assiduidadeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!continuation.isCompleted) {
                        val assiduidadeList =
                            snapshot.children.mapNotNull { it.getValue(Atrassos::class.java) }
                        continuation.resume(assiduidadeList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(Exception("Erro ao acessar o banco de dados: ${error.message}"))
                    }
                }
            })
        }
    }

    private suspend fun getAssiduidadeStatistics(userId: String): Map<String, Double> {
        val assiduidadeRef = database.getReference("Assiduidade").child(userId)
        return suspendCancellableCoroutine { continuation ->
            assiduidadeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!continuation.isCompleted) {
                        val totalDias = snapshot.childrenCount
                        var diasPresentes = 0
                        var totalFaltas = 0

                        snapshot.children.forEach { assiduidadeSnapshot ->
                            val assiduidade = assiduidadeSnapshot.getValue(Atrassos::class.java)
                            if (assiduidade != null) {
                                if (assiduidade.present) {
                                    diasPresentes++
                                }
                                totalFaltas += assiduidade.faltas
                            }
                        }

                        val porcentagemPresenca = if (totalDias > 0) {
                            (diasPresentes.toDouble() / totalDias) * 100
                        } else {
                            0.0
                        }

                        val statistics = mapOf(
                            "totalDias" to totalDias.toDouble(),
                            "diasPresentes" to diasPresentes.toDouble(),
                            "totalFaltas" to totalFaltas.toDouble(),
                            "porcentagemPresenca" to porcentagemPresenca
                        )

                        continuation.resume(statistics)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(Exception("Erro ao acessar o banco de dados: ${error.message}"))
                    }
                }
            })
        }
    }
}

