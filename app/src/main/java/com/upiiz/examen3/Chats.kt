package com.upiiz.examen3

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Chats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: MutableList<Chat>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats)

        // Ajustar la vista para respetar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el usuario actual del Intent
        currentUser = intent.getStringExtra("currentUser") ?: "usuario_desconocido"

        // Inicializar RecyclerView y lista
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatList = mutableListOf()

        // Inicializar el adaptador después de asignar currentUser
        chatAdapter = ChatAdapter(chatList, currentUser)
        recyclerView.adapter = chatAdapter

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("chats")

        // Cargar chats
        cargarChats()
    }

    private fun cargarChats() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat != null && (chat.usuario1 == currentUser || chat.usuario2 == currentUser)) {
                        chatList.add(chat)
                    }
                }
                chatAdapter.notifyDataSetChanged() // Notificar cambios al adaptador
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Chats", "Error al cargar chats", error.toException())
            }
        })
    }
}
