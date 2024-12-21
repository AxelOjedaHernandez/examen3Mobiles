package com.upiiz.examen3

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
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

    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        // Obtener el userId desde el intent
        currentUserId = intent.getStringExtra("userId").toString()
        if (currentUserId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se encontró el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        messageList = mutableListOf()

        adapter = MessageAdapter(messageList, currentUserId)
        recyclerView.adapter = adapter

        // Obtener chatId desde el intent
        val chatId = intent.getStringExtra("chatId") ?: return

        // Inicializar referencia a la base de datos
        database = FirebaseDatabase.getInstance().getReference("mensajes").child(chatId)

        cargarMensajes()

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
                    if (message != null) {
                        messageList.add(message)
                    }
                }
                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messageList.size - 1) // Mover al último mensaje
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Toast.makeText(this@MessagesActivity, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enviarMensaje(chatId: String) {
        val texto = inputMessage.text.toString().trim()
        if (texto.isNotEmpty()) {
            val message = Message(texto, currentUserId, System.currentTimeMillis())
            val key = database.push().key
            key?.let {
                database.child(it).setValue(message)
                inputMessage.text.clear()
            }
        }
    }
}





