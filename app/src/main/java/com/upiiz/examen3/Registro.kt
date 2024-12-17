package com.upiiz.examen3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    // Declarar variables globales para los EditText y el botón
    private lateinit var etUsuario: EditText
    private lateinit var etNombre: EditText
    private lateinit var etContraseña: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // Ajustar la vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuarioRegistro)
        etNombre = findViewById(R.id.etNombreRegistro)
        etContraseña = findViewById(R.id.etContraseñaRegistro)
        etCorreo = findViewById(R.id.etCorreoRegistro)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRegresar = findViewById(R.id.btnRegresarRegistro)

        // Configurar el botón Guardar
        btnGuardar.setOnClickListener {
            guardarUsuarioEnFirebase()
        }

        // Botón Regresar (cierra la actividad)
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun guardarUsuarioEnFirebase() {
        // Obtener datos de los EditText
        val userId = etUsuario.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contraseña = etContraseña.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (userId.isEmpty() || nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear una referencia a Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val usuariosRef = database.getReference("usuarios")

        // Crear un objeto con los datos del usuario
        val usuario = mapOf(
            "nombre" to nombre,
            "email" to correo,
            "contraseña" to contraseña // Puedes decidir si guardas contraseñas, aunque no es recomendado en texto plano
        )

        // Guardar el usuario en la base de datos
        usuariosRef.child(userId).setValue(usuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiarCampos() {
        // Limpiar los campos después de guardar
        etUsuario.text.clear()
        etNombre.text.clear()
        etContraseña.text.clear()
        etCorreo.text.clear()
    }
}
