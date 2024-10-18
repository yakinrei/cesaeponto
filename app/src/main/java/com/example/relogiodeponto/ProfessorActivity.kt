package com.example.relogiodeponto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import aulasAdapter
import com.example.relogiodeponto.databinding.ActivityProfessorBinding
import com.google.firebase.database.*
import modulosAdapter

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
    private lateinit var polosRef: DatabaseReference

    private var materiasList = mutableListOf<Materias>()
    private var cursosEdicaoList = mutableListOf<CursoEdicao>()
    private var listaMateriasList = mutableListOf<ListaMaterias>()
    private var polosList = mutableListOf<Polos>()

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

        // Configurar o RecyclerView do calendário (aulas)
        aulasAdapter = aulasAdapter(emptyList())
        binding.rvCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCalendar.adapter = aulasAdapter

        // Carregar todas as listas do Firebase
        carregarMaterias()
        carregarCursosEdicao()
        carregarListaMaterias()
        carregarPolos()
        carregarModulos()
    }

    private fun carregarMaterias() {
        materiasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                materiasList.clear()
                for (materiaSnapshot in snapshot.children) {
                    val materia = materiaSnapshot.getValue(Materias::class.java)
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
                for (cursoSnapshot in snapshot.children) {
                    val curso = cursoSnapshot.getValue(CursoEdicao::class.java)
                    curso?.let { cursosEdicaoList.add(it) }
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
                for (listaSnapshot in snapshot.children) {
                    val lista = listaSnapshot.getValue(ListaMaterias::class.java)
                    lista?.let { listaMateriasList.add(it) }
                }
                atualizarModulosAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }

    private fun carregarPolos() {
        polosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                polosList.clear()
                for (poloSnapshot in snapshot.children) {
                    val polo = poloSnapshot.getValue(Polos::class.java)
                    polo?.let { polosList.add(it) }
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
                carregarAulas(modulosList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Lidar com erros
            }
        })
    }


    private fun carregarAulas(modulos: List<Modulos>) {
        val modulosIds = modulos.map { it.id }
        val dataAtual = System.currentTimeMillis()

        aulasRef.orderByChild("data").startAt(dataAtual.toDouble()).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val aulasList = mutableListOf<Aulas>()
                for (aulaSnapshot in snapshot.children) {
                    val aula = aulaSnapshot.getValue(Aulas::class.java)
                    if (aula != null && modulosIds.contains(aula.moduloId)) {
                        aulasList.add(aula)
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
        // Atualiza o adapter dos módulos quando todos os dados necessários forem carregados
        modulosAdapter.updateModulos(modulosAdapter.modulos, materiasList, cursosEdicaoList, listaMateriasList)
    }
}
