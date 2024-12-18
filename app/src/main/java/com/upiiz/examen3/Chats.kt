package com.upiiz.examen3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.upiiz.examen3.adapters.ChatAdapter
import com.upiiz.examen3.models.Chat
import com.upiiz.examen3.models.Message

class Chats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var chatList: MutableList<Chat>
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatList = mutableListOf()
        adapter = ChatAdapter(chatList) { chat ->
            // Ir a la actividad de mensajes
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("chatId", chat.chatId) // Pasar el chatId
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Referencia a Firebase
        database = FirebaseDatabase.getInstance().getReference()

        // Cargar los chats
        verificarODefinirDatosDeEjemplo()
        cargarChats()
    }

    private fun cargarChats() {
        val chatsRef = database.child("chats")
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    chat?.let { chatList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Chats, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verificarODefinirDatosDeEjemplo() {
        val chatsRef = database.child("chats")
        val mensajesRef = database.child("mensajes")

        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Insertar chats de ejemplo
                    val chat1 = Chat("chat1", "Maradona", "Es la mano de Dios", "ayer")
                    val chat2 = Chat("chat2", "Zague", "Vamos al partido", "12:54")
                    val chat3 = Chat("chat3", "Luis", "Nos vemos luego", "hoy")

                    chatsRef.child("chat1").setValue(chat1)
                    chatsRef.child("chat2").setValue(chat2)
                    chatsRef.child("chat3").setValue(chat3)

                    // Insertar mensajes para cada chat
                    insertarMensajesDeEjemplo(mensajesRef, "chat1", listOf(
                        Message("Cuántos goles?", "user1", System.currentTimeMillis()),
                        Message("No recuerdo", "user2", System.currentTimeMillis()),
                        Message("Es la mano de Dios", "user1", System.currentTimeMillis())
                    ))

                    insertarMensajesDeEjemplo(mensajesRef, "chat2", listOf(
                        Message("Vamos al partido", "user3", System.currentTimeMillis()),
                        Message("A qué hora?", "user4", System.currentTimeMillis()),
                        Message("A las 7 PM", "user3", System.currentTimeMillis())
                    ))

                    insertarMensajesDeEjemplo(mensajesRef, "chat3", listOf(
                        Message("Nos vemos luego", "user5", System.currentTimeMillis()),
                        Message("Claro, cuídate", "user6", System.currentTimeMillis()),
                        Message("Igualmente!", "user5", System.currentTimeMillis())
                    ))

                    Toast.makeText(this@Chats, "Datos de ejemplo insertados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Chats, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun insertarMensajesDeEjemplo(mensajesRef: DatabaseReference, chatId: String, mensajes: List<Message>) {
        val chatMensajesRef = mensajesRef.child(chatId)
        for (mensaje in mensajes) {
            val mensajeKey = chatMensajesRef.push().key // Generar ID único
            if (mensajeKey != null) {
                chatMensajesRef.child(mensajeKey).setValue(mensaje)
            }
        }
    }
}




