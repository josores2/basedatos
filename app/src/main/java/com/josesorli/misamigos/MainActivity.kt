package com.josesorli.misamigos

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
    private lateinit var provinciaEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var consultaButton : Button
    private lateinit var provinciaButton : Button
    private lateinit var consultaNombreTextView : TextView

    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Capturamos objetos de los EditText para guardar en BBDD
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        provinciaEditText = findViewById((R.id.provinciaEditText))

        //Capturamos objetos botones y TextView
        saveButton = findViewById(R.id.saveButton)
        consultaButton = findViewById(R.id.consultaButton)
        consultaNombreTextView = findViewById(R.id.consultaNombreTextView)

        provinciaButton = findViewById(R.id.provinciaButton)



        db = DatabaseHandler(this)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val provincia = provinciaEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && provincia.isNotEmpty()) {
                val id = db.addContact(name, email, provincia)
                if (id != -1L) {
                    // Éxito al guardar en la base de datos
                    // Puedes mostrar un mensaje de éxito o realizar alguna otra acción aquí
                    nameEditText.text.clear()
                    emailEditText.text.clear()
                    provinciaEditText.text.clear()
                } else {
                    // Ocurrió un error al guardar en la base de datos
                    // Puedes mostrar un mensaje de error o realizar alguna otra acción aquí
                    Toast.makeText(applicationContext, "PROBLEMA AL GUARDAR EN LA BBDD", Toast.LENGTH_LONG).show()
                }
            } else {
                // Los campos están vacíos, muestra un mensaje de error o realiza alguna otra acción aquí
                Toast.makeText(applicationContext, "Te falta algún campo por rellenar", Toast.LENGTH_SHORT).show()
            }
        }

        consultaButton.setOnClickListener {
            db = DatabaseHandler(this)
            val contactList = db.getAllContacts()
            consultaNombreTextView.text = ""
            val nombresTexto = contactList.joinToString()
            //Mostramos en el textView la Lista que ha devuelto getAllContacts()
            //consultaTextView.text = nombresTexto

            /*Mostramos contactos en el LogCat
            for (contact in contactList) {
                Log.d("Contacto","ID: ${contact.id}, Nombre: ${contact.name}, Email: ${contact.email}")
            }*/
            //Mostramos contactos de forma ordenada, recorriendo la Lista
            for (contact in contactList) {
                consultaNombreTextView.append("NOMBRE: ${contact.name} -- EMAIL: ${contact.email} -- PROVINCIA: ${contact.provincia}\n")
            }
        }

  /*      provinciaButton.setOnClickListener {
            db = DatabaseHandler(this)


            val contactList = db.getAllContacts()

            val nombresTexto = contactList.joinToString()
            //Limpiampos el textView de la consulta
            consultaNombreTextView.text = ""

            //Mostramos en el textView la Lista que ha devuelto getAllContacts()
            //consultaTextView.text = contactList.joinToString

            //Mostramos contactos en el LogCat
            /*for (contact in contactList) {

                if (contact.id == id1) {
                    nombreIDtextView.text = contact.name
                    emailIDtextView2.text = contact.email
                }
            }*/
    //Mostramos contactos de forma ordenada, recorriendo la Lista
            for (contact in contactList) {
                consultaNombreTextView.append("NOMBRE: ${contact.name}\n")
                consultaEmailTextView.append("EMAIL: ${contact.email}\n")
            }
            //Podemos hacer lo mismo, pero insertando 2 textView con layout horizontal, uno para nombre y otro para correos

        }*/
    }
}

