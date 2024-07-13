package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnLogin : Button
    private lateinit var btnSignup : Button
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        edtEmail = findViewById(R.id.emailLogin)
        edtPassword = findViewById(R.id.passwordLogin)
        btnLogin = findViewById(R.id.loginButton)
        btnSignup = findViewById(R.id.signupLoginButton)
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser?.uid != null){
            val intent = Intent(this@LoginActivity , MainActivity::class.java)
            finish()
            startActivity(intent)
        }
        btnSignup.setOnClickListener {
            val intent = Intent(this , SignUpActivity::class.java)
            startActivity(intent)
        }
        
        btnLogin.setOnClickListener {
            var Email = edtEmail.text.toString()
            Email = Email.trimEnd()
            val Password = edtPassword.text.toString()

            if(Email.isEmpty()){
                edtEmail.error = "Email is required"
            }
            else if(Password.isEmpty()){
                edtPassword.error = "Password is required"
            }
            else
               login(Email , Password)
        }
    }

    private fun login(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity , MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity , "User Does Not Exist" , Toast.LENGTH_SHORT).show()
                }
            }
    }
}