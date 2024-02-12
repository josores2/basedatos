package com.josesorli.misamigos

import android.content.Intent
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
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var provinciaEditText: EditText
    //private lateinit var provinciaID: EditText
    private lateinit var saveButton: Button
    private lateinit var consultaButton : Button
    private lateinit var consultaPrvButton : Button
    private lateinit var consultaNombreTextView : TextView
    private lateinit var spinnerID : Spinner

    private lateinit var db: DatabaseHandler

    //Creamos el objeto publi para cargar y mostrar la publicidad en esta actividad
    private lateinit var publi : publiHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializamos, controlamos el evento, cargamos y luego mostraremos la publicidad cargada
        publi = publiHandler(this)
        publi.inicializarPubli()
        publi.manejarAccionPublicidad()
        publi.cargarPublicidad()


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
        consultaPrvButton = findViewById(R.id.consultaPrvButton)

        var TAG = this@MainActivity.javaClass.simpleName
        //Quitamos el botón de provincia pq esta funcionalidad ya la hace el spinner
        //provinciaButton = findViewById(R.id.provinciaButton)

        db = DatabaseHandler(this)

        //Instancia para conectar con la BBDD de Firebase
        val dbF = FirebaseFirestore.getInstance()

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
                //Mostramos la publicidad después de desplegar el spinner
                publi.mostrarPubli()

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

            //Inserción de la colección y los campos, clave email
            //Si queremos ID automático, cambiamos document.set por add.
            dbF.collection("usuarios").document(email).set(
                hashMapOf("nombre" to name,
                    "provincia" to provincia
                    )
            ).addOnSuccessListener {
                Log.d(TAG, "Documento creado exitosamente")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error al crear el documento", e)
            }


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
            val email = emailEditText.text.toString().trim()
            consultaNombreTextView.text = ""
            //val nombresTexto = contactList.joinToString()
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
            if (email.isNotEmpty())
                //Muestro en los mismos EditText, el nombre y la provincia del email que introduzca
                dbF.collection("usuarios").document(email).get().addOnSuccessListener {
                    nameEditText.setText(it.get("nombre") as String?)
                    provinciaEditText.setText(it.get("provincia") as String?)
                }
        }

        consultaPrvButton.setOnClickListener {

            val provincia = provinciaEditText.text.toString().trim()
            consultaNombreTextView.text = ""


            if (provincia.isNotEmpty()) {
                dbF.collection("usuarios")
                    .whereEqualTo("provincia", provincia)
                    .get()
                    .addOnSuccessListener { result ->

                        for (document in result) {
                            val nm = document.getString("nombre") ?: ""
                            val em = document.id //Para consultar el ID único, en este caso, el email.
                            val prv = document.getString("provincia") ?: ""
                            consultaNombreTextView.append(
                                " FB NOMBRE: $nm " +
                                        "-- EMAIL: $em " +
                                        "-- PROVINCIA: $prv\n"
                            )
                        }
                    }
            }
        }


    }

}

