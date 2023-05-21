package com.example.contagemglicemia.Modules.Report.pages.page1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contagemglicemia.Model.Glicemia
import com.example.contagemglicemia.R
import java.text.SimpleDateFormat

class RegistroGlicemiaAdapter(val registros: List<Glicemia>, val onItemClick: (Glicemia) -> Unit) : RecyclerView.Adapter<RegistroGlicemiaAdapter.RegistroGlicemiaViewHolder>() {

    class RegistroGlicemiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewValor: TextView = itemView.findViewById(R.id.text_view_valor)
        val textViewInsulina: TextView = itemView.findViewById(R.id.text_view_insulina)
        val textViewData: TextView = itemView.findViewById(R.id.text_view_data)
        val textViewId: TextView = itemView.findViewById(R.id.text_view_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroGlicemiaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_report_glicemy, parent, false)
        return RegistroGlicemiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RegistroGlicemiaViewHolder, position: Int) {
        val registro = registros[position]

        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
       // val dataFormatada = dateFormat.format(registro.date)

        holder.textViewValor.text = registro.value.toString()
        holder.textViewInsulina.text = registro.insulina_apply.toString()
        holder.textViewData.text = registro.date.toString()
        holder.textViewId.text = registro.id.toString()

        holder.itemView.setOnClickListener { onItemClick(registro) }
    }

    override fun getItemCount() = registros.size
}
