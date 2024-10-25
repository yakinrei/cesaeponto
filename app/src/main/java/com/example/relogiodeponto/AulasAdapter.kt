
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.relogiodeponto.Aulas
import com.example.relogiodeponto.R
import com.example.relogiodeponto.databinding.ItemDiaCalendarioBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class aulasAdapter(
    private var aulas: List<Aulas>
) : RecyclerView.Adapter<aulasAdapter.AulaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val binding = ItemDiaCalendarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AulaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        val aula = aulas[position]
        holder.bind(aula)
    }

    override fun getItemCount(): Int = aulas.size

    fun updateAulas(newAulas: List<Aulas>) {
        aulas = newAulas
        notifyDataSetChanged()
    }

    fun update(aulasList: MutableList<Aulas>) {
        aulas = aulasList
        notifyDataSetChanged()
    }

    class AulaViewHolder(private val binding: ItemDiaCalendarioBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(aula: Aulas) {
            // Parsear a data
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(aula.diaprevisto)

            // Formatar para pegar o dia e o mês
            val diaFormat = SimpleDateFormat("dd", Locale.getDefault())
            val mesFormat = SimpleDateFormat("MMM", Locale.getDefault()).apply {
                isLenient = false
            }

            val dia = diaFormat.format(date ?: Date()) // Pega o dia
            val mes = mesFormat.format(date ?: Date()).uppercase(Locale.getDefault()) // Pega o mês abreviado em maiúsculas

            binding.tvDia.text = dia
            binding.tvMes.text = mes

            when (aula.turno) {
                1 -> {
                    binding.circleView.background = binding.root.context.getDrawable(R.drawable.workdayiconmanha)
                }
                2 -> {
                    binding.circleView.background = binding.root.context.getDrawable(R.drawable.workdayicontarde)
                }
                3 -> {
                    binding.circleView.background = binding.root.context.getDrawable(R.drawable.workdayiconnoite)
                }
                else -> {
                    binding.circleView.background = binding.root.context.getDrawable(R.drawable.workdayiconall)
                }
            }
        }
    }
}
