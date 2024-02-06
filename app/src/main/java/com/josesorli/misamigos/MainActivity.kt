package com.josesorli.misamigos

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var provinciaEditText: EditText
    private lateinit var provinciaID: EditText
    private lateinit var saveButton: Button
    private lateinit var consultaButton : Button
    private lateinit var provinciaButton : Button
    private lateinit var consultaNombreTextView : TextView
    private lateinit var spinnerID : Spinner

    private lateinit var db: DatabaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Capturamos objetos de los EditText para guardar en BBDD
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        provinciaEditText = findViewById((R.id.provinciaEditText))
        //provinciaID = findViewById((R.id.provinciaID))
        spinnerID = findViewById(R.id.spinnerID)


        //Capturamos objetos botones y TextView
        saveButton = findViewById(R.id.saveButton)
        consultaButton = findViewById(R.id.consultaButton)
        consultaNombreTextView = findViewById(R.id.consultaNombreTextView)

        provinciaButton = findViewById(R.id.provinciaButton)



        db = DatabaseHandler(this)

        var provArray = db.selecProvUnica()
        var spinnerID2:String = ""

        //Definimos el adaptador del Array, necesario para definir los elementos que contiene el desplegable
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Asignamos el aaptador al Spinner de xml
        spinnerID.adapter = adapter
        //Capturamos el evento del spinner, cuando el usuario selecciona algún elemento
        spinnerID.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            //Le damos los argumentos al spinner. Hemos de conocer la posición del array que ha seleccionado el usuario
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                spinnerID2 = provArray[position].toString()
                if (spinnerID2 != "") {
                    Toast.makeText(
                        this@MainActivity,
                        "Has seleccionado la provincia:" + provArray[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                consultaNombreTextView.text = ""
                val contactList = db.queryProvinciaContacts(spinnerID2)
                for (contact in contactList) {
                    val id = contact.id
                    val name = contact.name
                    val email = contact.email
                    val provincia = contact.provincia
                    consultaNombreTextView.append("$id $name $email $provincia \n")
                }
            }
            //Función necesaria para contemplar la opción de que no haya nada seleccionado en el desplegable
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

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

        provinciaButton.setOnClickListener {
            db = DatabaseHandler(this)
            //spinnerId.get()
            val prv = provinciaID.text.toString().trim()
            val contactList = db.getProvinciaContacts(prv)
            consultaNombreTextView.text = ""

            //Mostramos contactos de forma ordenada, recorriendo la Lista
            for (contact in contactList) {
                consultaNombreTextView.append("NOMBRE: ${contact.name} -- EMAIL: ${contact.email} -- PROVINCIA: ${contact.provincia}\n")
            }
        }
    }
}

