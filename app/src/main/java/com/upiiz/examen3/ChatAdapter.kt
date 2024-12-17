package com.upiiz.examen3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val chatsList: List<Chat>,
    private val currentUser: String
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatsList[position]

        // Mostrar el nombre del otro usuario
        val otherUser = if (chat.usuario1 == currentUser) chat.usuario2 else chat.usuario1

        holder.nameTextView.text = otherUser
        holder.messageTextView.text = chat.ultimoMensaje
        holder.timeTextView.text = chat.hora

        // Imagen de perfil (ejemplo con imagen por defecto)
        holder.profileImage.setImageResource(R.drawable.ic_person)
    }

    override fun getItemCount(): Int = chatsList.size
}
