package com.upiiz.examen3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.upiiz.examen3.adapters.ChatAdapter;
import com.upiiz.examen3.models.Chat;

class Chats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView;
    private lateinit var database: DatabaseReference;
    private lateinit var chatList: MutableList<Chat>;
    private lateinit var adapter: ChatAdapter;
    private lateinit var userId: String; // ID del usuario que inició sesión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        // Obtener el ID del usuario que inició sesión (se debe pasar como extra al iniciar esta actividad)
        userId = intent.getStringExtra("userId") ?: "";
        if (userId.isEmpty()) {
            Toast.makeText(this, "Error: No se encontró el usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.layoutManager = LinearLayoutManager(this);

        chatList = mutableListOf();
        adapter = ChatAdapter(chatList, userId) { chat ->
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("userId", userId) // Pasar también el userId
            startActivity(intent)
        }

        recyclerView.adapter = adapter;

        // Referencia a Firebase
        database = FirebaseDatabase.getInstance().getReference();

        // Cargar los chats para el usuario actual
        cargarChats();
    }

    private fun cargarChats() {
        val chatsRef = database.child("chats")

        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    // Asignar el ID del chat desde la clave del nodo
                    if (chat != null) {
                        chat.chatId = dataSnapshot.key ?: ""

                        // Verificar si el chat pertenece al usuario actual
                        if (chat.user1 == userId || chat.user2 == userId) {
                            // Si no hay mensajes, asignar valores predeterminados
                            if (chat.ultimoMensaje.isEmpty()) {
                                chat.ultimoMensaje = "Sin mensajes aún"
                                chat.horaUltimoMensaje = ""
                            }

                            // Agregar chat a la lista
                            chatList.add(chat)
                        }
                    }
                }

                // Actualizar el adaptador
                if (chatList.isEmpty()) {
                    Toast.makeText(this@Chats, "No hay chats disponibles", Toast.LENGTH_SHORT).show()
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Chats, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}





