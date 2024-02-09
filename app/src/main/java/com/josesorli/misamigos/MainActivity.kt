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
import androidx.core.view.get
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError

//Importamos las librerías de AdMob de Google
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest

import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

/*import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase*/

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var provinciaEditText: EditText
    //private lateinit var provinciaID: EditText
    private lateinit var saveButton: Button
    private lateinit var consultaButton : Button
    //private lateinit var provinciaButton : Button
    private lateinit var consultaNombreTextView : TextView
    private lateinit var spinnerID : Spinner

    private lateinit var db: DatabaseHandler

    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private final var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdManagerInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        //Inicializamos llamada a la publicidad de adMob
        MobileAds.initialize(this) {}

        var adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(this,"/6499/example/interstitial", adRequest, object : AdManagerInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mAdManagerInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mAdManagerInterstitialAd = interstitialAd
            }
        })

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
        //Quitamos el botón de provincia pq esta funcionalidad ya la hace el spinner
        //provinciaButton = findViewById(R.id.provinciaButton)



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
                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd?.show(this@MainActivity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
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
        }

    }

}

