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
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewUltimoMensaje: TextView = itemView.findViewById(R.id.textViewUltimoMensaje)
        private val textViewHora: TextView = itemView.findViewById(R.id.textViewHora)

        fun bind(chat: Chat) {
            textViewNombre.text = chat.nombre
            textViewUltimoMensaje.text = chat.ultimoMensaje
            textViewHora.text = chat.hora

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


