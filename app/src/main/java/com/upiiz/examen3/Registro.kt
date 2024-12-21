package com.upiiz.examen3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

class Registro : AppCompatActivity() {

    // Declarar variables globales para los EditText y el botón
    private lateinit var etUsuario: EditText;
    private lateinit var etNombre: EditText;
    private lateinit var etContraseña: EditText;
    private lateinit var etCorreo: EditText;
    private lateinit var btnGuardar: Button;
    private lateinit var btnRegresar: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_registro);

        // Ajustar la vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            insets;
        };

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuarioRegistro);
        etNombre = findViewById(R.id.etNombreRegistro);
        etContraseña = findViewById(R.id.etContraseñaRegistro);
        etCorreo = findViewById(R.id.etCorreoRegistro);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnRegresar = findViewById(R.id.btnRegresarRegistro);

        // Configurar el botón Guardar
        btnGuardar.setOnClickListener {
            guardarUsuarioEnFirebase();
        };

        // Botón Regresar (cierra la actividad)
        btnRegresar.setOnClickListener {
            finish();
        };
    }

    private fun guardarUsuarioEnFirebase() {
        val userId = etUsuario.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        // Validar campos vacíos
        if (userId.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear referencia a Firebase
        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("usuarios")

        // Crear objeto usuario
        val usuario = mapOf(
            "nombre" to nombre,
            "email" to correo,
            "contraseña" to contraseña // Nota: Considera encriptar la contraseña
        )

        // Guardar el usuario
        usuariosRef.child(userId).setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                crearChatsConUsuariosExistentes(userId) // Crear chats al registrar el usuario
                limpiarCampos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun crearChatsConUsuariosExistentes(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("usuarios")
        val chatsRef = database.getReference("chats")

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (usuarioSnapshot in snapshot.children) {
                    val existingUserId = usuarioSnapshot.key

                    if (existingUserId != null && existingUserId != userId) {
                        // Generar un ID único para el chat
                        val chatId = if (userId < existingUserId) "$userId-$existingUserId" else "$existingUserId-$userId"

                        // Usar transacción para asegurar que no se creen duplicados
                        chatsRef.child(chatId).runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                if (currentData.value == null) {
                                    val chatData = mapOf(
                                        "user1" to userId,
                                        "user2" to existingUserId,
                                        "ultimoMensaje" to "",
                                        "horaUltimoMensaje" to ""
                                    )
                                    currentData.value = chatData
                                }
                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                                if (error != null) {
                                    Toast.makeText(this@Registro, "Error creando chat: ${error.message}", Toast.LENGTH_SHORT).show()
                                } else if (committed) {
                                    Toast.makeText(this@Registro, "Chat creado con $existingUserId", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Registro, "Error al leer usuarios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun limpiarCampos() {
        // Limpiar los campos después de guardar
        etUsuario.text.clear();
        etNombre.text.clear();
        etContraseña.text.clear();
        etCorreo.text.clear();
    }
}