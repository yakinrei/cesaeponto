package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import aulasAdapter
import com.example.relogiodeponto.databinding.ActivityProfessorBinding
import com.google.firebase.database.*
import modulosAdapter
import java.text.SimpleDateFormat
import java.util.*

class ProfessorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorBinding
    private lateinit var usuario: Usuario
    private lateinit var modulosAdapter: modulosAdapter
    private lateinit var aulasAdapter: aulasAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var modulosRef: DatabaseReference
    private lateinit var aulasRef: DatabaseReference
    private lateinit var materiasRef: DatabaseReference
    private lateinit var cursosEdicaoRef: DatabaseReference
    private lateinit var listaMateriasRef: DatabaseReference

    private var materiasList = mutableListOf<Materias>()
    private var cursosEdicaoList = mutableListOf<CursoEdicao>()
    private var listaMateriasList = mutableListOf<ListaMaterias>()
    private var aulasList = mutableListOf<Aulas>()
    private var modulosList = mutableListOf<Modulos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuario = intent.getSerializableExtra("Usuario") as Usuario
        binding.profName.text = usuario.nome
        binding.profEmail.text = usuario.email

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        modulosRef = database.getReference("Modulos")
        aulasRef = database.getReference("Aulas")
        materiasRef = database.getReference("Materias")
        cursosEdicaoRef = database.getReference("CursoEdicao")
        listaMateriasRef = database.getReference("ListaMaterias")

        // Configurar o RecyclerView dos módulos
        modulosAdapter = modulosAdapter(emptyList(), emptyList(), emptyList(), emptyList())
        binding.rvModulos.layoutManager = LinearLayoutManager(this)
        binding.rvModulos.adapter = modulosAdapter

        // Configurar o RecyclerView do calendário
        aulasAdapter = aulasAdapter(emptyList())
        binding.rvCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendar.adapter = aulasAdapter

        // Carregar todas as listas do Firebase
        carregarModulos()
        carregarAulas()
        carregarMaterias()
        carregarCursosEdicao()
        carregarListaMaterias()
    }

    private fun carregarMaterias() {
        materiasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                materiasList.clear()
                for (dataSnapshot in snapshot.children) {
                    val materia = dataSnapshot.getValue(Materias::class.java)
                    materia?.let { materiasList.add(it) }
                }
                atualizarModulosAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun carregarCursosEdicao() {
        cursosEdicaoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cursosEdicaoList.clear()
                for (dataSnapshot in snapshot.children) {
                    val cursoEdicao = dataSnapshot.getValue(CursoEdicao::class.java)
                    cursoEdicao?.let { cursosEdicaoList.add(it) }
                }
                atualizarModulosAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun carregarListaMaterias() {
        listaMateriasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMateriasList.clear()
                for (dataSnapshot in snapshot.children) {
                    val listaMateria = dataSnapshot.getValue(ListaMaterias::class.java)
                    listaMateria?.let { listaMateriasList.add(it) }
                }
                atualizarModulosAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun carregarModulos() {
        modulosRef.orderByChild("usuarioId").equalTo(usuario.Id.toDouble()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val modulosList = mutableListOf<Modulos>()
                for (moduloSnapshot in snapshot.children) {
                    val modulo = moduloSnapshot.getValue(Modulos::class.java)
                    modulo?.let { modulosList.add(it) }
                }
                modulosAdapter.updateModulos(modulosList, materiasList, cursosEdicaoList, listaMateriasList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun carregarAulas() {
        val modulosIds = modulosAdapter.modulos.map { it.id }
        val dataAtual = System.currentTimeMillis()

        aulasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val aulasList = mutableListOf<Aulas>()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                for (aulaSnapshot in snapshot.children) {
                    val aula = aulaSnapshot.getValue(Aulas::class.java)
                    if (aula != null && modulosIds.contains(aula.moduloId)) {
                        val dataPrevista = aula.diaprevisto // Presumindo que 'diaprevisto' é uma String
                        try {
                            val dataPrevistaDate = dateFormat.parse(dataPrevista)
                            val dataPrevistaTimestamp = dataPrevistaDate?.time ?: 0

                            if (dataPrevistaTimestamp >= dataAtual) {
                                aulasList.add(aula)
                            }
                        } catch (e: Exception) {
                            // Lidar com erros de conversão de data
                            e.printStackTrace()
                        }
                    }
                }
                aulasAdapter.updateAulas(aulasList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun atualizarModulosAdapter() {
        modulosAdapter.updateModulos(modulosAdapter.modulos, materiasList, cursosEdicaoList, listaMateriasList)
    }

    private fun atualizarAulasAdapter() {
        aulasAdapter.update(aulasList)
    }

}

