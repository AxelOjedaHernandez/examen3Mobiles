package com.upiiz.examen3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class Login : AppCompatActivity() {

    // Declarar variables globales para las vistas y Firebase
    private lateinit var etUsuario: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnIngresar: Button
    private lateinit var btnRegistrarse: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Ajustar vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario)
        etContraseña = findViewById(R.id.etContraseña)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnRegistrarse = findViewById(R.id.btnRegresarInicio)

        // Referencia a Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios")

        // Configurar el botón Ingresar
        btnIngresar.setOnClickListener {
            realizarLogin()
        }

        // Botón Registrarse (lleva al Registro)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    private fun realizarLogin() {
        val usuarioId = etUsuario.text.toString().trim()
        val contraseñaIngresada = etContraseña.text.toString().trim()

        // Validar campos vacíos
        if (usuarioId.isEmpty() || contraseñaIngresada.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Consultar en Firebase si el usuario existe y la contraseña coincide
        databaseReference.child(usuarioId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Extraer la contraseña almacenada
                    val contraseñaGuardada = snapshot.child("contraseña").getValue(String::class.java)

                    // Verificar si la contraseña coincide
                    if (contraseñaGuardada == contraseñaIngresada) {
                        Toast.makeText(this@Login, "Login exitoso", Toast.LENGTH_SHORT).show()

                        // Crear el Intent y agregar el usuario como extra
                        val intent = Intent(this@Login, Chats::class.java)
                        intent.putExtra("currentUser", usuarioId) // Pasar el ID del usuario
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Login, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Login, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
