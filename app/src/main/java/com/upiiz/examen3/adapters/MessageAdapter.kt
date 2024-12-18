package com.upiiz.examen3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upiiz.examen3.R
import com.upiiz.examen3.models.Message

class MessageAdapter(
    private val messageList: List<Message>,
    private val userId: String // El ID del usuario actual para diferenciar mensajes
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.userId == userId) {
            R.layout.item_message_sent // Layout para mensajes enviados
        } else {
            R.layout.item_message_received // Layout para mensajes recibidos
        }
    }

    override fun getItemCount(): Int = messageList.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textMessage)
        private val timeText: TextView = itemView.findViewById(R.id.textTime)

        fun bind(message: Message) {
            messageText.text = message.texto
            timeText.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
        }
    }
}



