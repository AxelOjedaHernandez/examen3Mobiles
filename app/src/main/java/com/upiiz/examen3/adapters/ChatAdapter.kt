package com.upiiz.examen3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upiiz.examen3.R
import com.upiiz.examen3.models.Chat

class ChatAdapter(
    private val chatList: List<Chat>,
    private val userId: String,
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewUltimoMensaje: TextView = itemView.findViewById(R.id.textViewUltimoMensaje)
        private val textViewHora: TextView = itemView.findViewById(R.id.textViewHora)

        fun bind(chat: Chat) {
            // Determinar el nombre del otro usuario
            val nombre = if (chat.user1 == userId) chat.user2 else chat.user1
            textViewNombre.text = nombre // Mostrar el nombre del otro usuario

            // Mostrar último mensaje y hora
            textViewUltimoMensaje.text = chat.ultimoMensaje.ifEmpty { "Sin mensajes aún" }
            textViewHora.text = chat.horaUltimoMensaje.ifEmpty { "" }

            // Configurar evento de clic
            itemView.setOnClickListener { onItemClick(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount() = chatList.size
}



