package com.josesorli.misamigos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class Login : AppCompatActivity() {

    //Variables para firebase
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    //private lateinit var txtEmail: EditText
    private lateinit var progressBarLogin: ProgressBar
    //private lateinit var dbReference: DatabaseReference
    //private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUser = findViewById(R.id.txtUser)
        txtPassword = findViewById(R.id.txtPassword)
        progressBarLogin = findViewById(R.id.progressBarLogin)

        auth = FirebaseAuth.getInstance()
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