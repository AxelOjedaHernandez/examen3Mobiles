package com.upiiz.examen3

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.upiiz.examen3.adapters.MessageAdapter
import com.upiiz.examen3.models.Message

class MessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var database: DatabaseReference
    private lateinit var messageList: MutableList<Message>

    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton

    private val userId = "user1" // Simulación del usuario actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        recyclerView = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        messageList = mutableListOf()

        adapter = MessageAdapter(messageList, userId)
        recyclerView.adapter = adapter

        // Obtener chatId del intent
        val chatId = intent.getStringExtra("chatId") ?: return

        // Cargar mensajes desde Firebase
        database = FirebaseDatabase.getInstance().getReference("mensajes").child(chatId)
        cargarMensajes()

        // Evento para enviar mensaje
        sendButton.setOnClickListener {
            enviarMensaje(chatId)
        }
    }

    private fun cargarMensajes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun enviarMensaje(chatId: String) {
        val texto = inputMessage.text.toString().trim()
        if (texto.isNotEmpty()) {
            val message = Message(texto, userId, System.currentTimeMillis())
            val key = database.push().key
            key?.let {
                database.child(it).setValue(message)
                inputMessage.text.clear()
            }
        }
    }
}


