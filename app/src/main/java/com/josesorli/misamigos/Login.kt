package com.josesorli.misamigos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

//Importaciones necesarias para autenticación con Google
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class Login : AppCompatActivity() {

    //Variables para firebase
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    //private lateinit var txtEmail: EditText
    private lateinit var progressBarLogin: ProgressBar
    //private lateinit var dbReference: DatabaseReference
    //private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    //Botón de login con Google
    private lateinit var btn_google_login:Button

    //Declaraciones para login con Google
    //private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUser = findViewById(R.id.txtUser)
        txtPassword = findViewById(R.id.txtPassword)
        progressBarLogin = findViewById(R.id.progressBarLogin)
        btn_google_login = findViewById((R.id.btn_google_login))

        auth = FirebaseAuth.getInstance()

        // Configure opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Configurar el cliente de inicio de sesión de Google
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el clic del botón de inicio de sesión con Google
        btn_google_login.setOnClickListener {
            signInWithGoogle()
        }
    }

    //Función para llamar al login de Google
    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //Si ha seleccionado cuenta de Google y se ha logeado con éxito, agregamos usuario Google autenticado
    //a nuestro proyecto de Firebase
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Autenticar con Firebase con la cuenta de Google obtenida
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Error al iniciar sesión con Google
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Función para obtener las credenciales del usuario de Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticación con Google exitosa, el usuario está conectado
                    Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    // Fallo en la autenticación con Google
                    Toast.makeText(this, "Error en la autenticación con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun forgotPassword(view:View) {
        startActivity(Intent(this,ForgotPass::class.java))
    }
    fun registrar(view:View) {
        startActivity(Intent(this,Registrar::class.java))
    }
    fun login(view:View) {
        loginUser()
    }

    private fun loginUser(){

        val user:String = txtUser.text.toString()
        val password:String = txtPassword.text.toString()

        if(!TextUtils.isEmpty(user)  && !TextUtils.isEmpty(password)){
            progressBarLogin.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(user,password).addOnCompleteListener(this){
                task->
                if (task.isSuccessful) {
                    action()
                }else{
                    Toast.makeText(this,"Error en la autenticación",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun action(){
        startActivity(Intent(this,MainActivity::class.java))
    }
}