import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.relogiodeponto.*
import com.example.relogiodeponto.databinding.ItemModuloBinding

class modulosAdapter(
    var modulos: List<Modulos>,
    private var materias: List<Materias>, // Lista de todas as matérias
    private var cursosEdicao: List<CursoEdicao>, // Lista de todos os cursos
    private var listaMaterias: List<ListaMaterias> // Lista de todas as listas de matérias
) : RecyclerView.Adapter<modulosAdapter.ModuloViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuloViewHolder {
        val binding = ItemModuloBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuloViewHolder, position: Int) {
        val modulo = modulos[position]
        val materia = materias.find { it.id == modulo.materiaId } // Encontra a matéria correspondente
        val cursoEdicao = cursosEdicao.find { it.id == modulo.cursoEdicaoId } // Encontra o curso correspondente
        val listaMateria = listaMaterias.find { it.id == cursoEdicao?.ListaMateriasId } // Encontra a lista de matérias

        holder.bind(modulo, materia, cursoEdicao, listaMateria)
    }

    override fun getItemCount(): Int = modulos.size

    fun updateModulos(newModulos: List<Modulos>, newMaterias: List<Materias>, newCursos: List<CursoEdicao>, newListaMaterias: List<ListaMaterias>) {
        modulos = newModulos
        materias = newMaterias
        cursosEdicao = newCursos
        listaMaterias = newListaMaterias
        notifyDataSetChanged()
    }

    class ModuloViewHolder(private val binding: ItemModuloBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(modulo: Modulos, materia: Materias?, cursoEdicao: CursoEdicao?, listaMateria: ListaMaterias?) {
            // Exibe o nome da matéria
            binding.modulos.text = materia?.nome ?: "Matéria Desconhecida"

            binding.curso.text = listaMateria?.nome ?: "Curso Desconhecido"

            // Verifica o polo e exibe no binding.polo
            binding.polo.text = when (cursoEdicao?.poloId) {
                1 -> "Porto"
                2 -> "Braga"
                3 -> "Online"
                else -> "Polo Desconhecido"
            }

        }
    }
}
