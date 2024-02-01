package com.josesorli.misamigos

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var consultaButton : Button
    private lateinit var consultaTextView : TextView


    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        saveButton = findViewById(R.id.saveButton)
        consultaButton = findViewById(R.id.consultaButton)
        consultaTextView = findViewById(R.id.consultaTextView)

        db = DatabaseHandler(this)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                val id = db.addContact(name, email)
                if (id != -1L) {
                    // Éxito al guardar en la base de datos
                    // Puedes mostrar un mensaje de éxito o realizar alguna otra acción aquí
                    nameEditText.text.clear()
                    emailEditText.text.clear()
                } else {
                    // Ocurrió un error al guardar en la base de datos
                    // Puedes mostrar un mensaje de error o realizar alguna otra acción aquí
                }
            } else {
                // Los campos están vacíos, muestra un mensaje de error o realiza alguna otra acción aquí
                Toast.makeText(applicationContext, "Te falta algún campo por rellenar", Toast.LENGTH_SHORT).show()
            }
        }

        consultaButton.setOnClickListener {
            db = DatabaseHandler(this)
            val contactList = db.getAllContacts()
            consultaTextView.text = ""
            val nombresTexto = contactList.joinToString()
            //Mostramos en el textView la Lista que ha devuelto getAllContacts()
            consultaTextView.text = nombresTexto

            //Mostramos contactos en el LogCat
            for (contact in contactList) {
                Log.d("Contacto","ID: ${contact.id}, Nombre: ${contact.name}, Email: ${contact.email}")
            }
        }
        }
    }

