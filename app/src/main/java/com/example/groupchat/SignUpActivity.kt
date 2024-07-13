package com.example.groupchat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var signUpButton : Button
    private lateinit var userName : EditText
    private lateinit var image : CircleImageView
    private lateinit var mAuth : FirebaseAuth
    private lateinit var uri : Uri
    private  var x : String? = null
    private lateinit var mDbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        email = findViewById(R.id.emailSignup)
        password = findViewById(R.id.passwordSignup)
        userName = findViewById(R.id.userNameSignup)
        signUpButton = findViewById(R.id.signupButton)
        image = findViewById(R.id.userProfileImageSignup)
        mAuth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener {
            var Email = email.text.toString()
            Email = Email.trimEnd()
            val Password = password.text.toString()
            val UserName = userName.text.toString()

            if(Email.isEmpty()){
                email.error = "Email is required"
            }
            else if(Password.isEmpty()){
                password.error = "Password is required"
            }
            else if(Password.length < 6) {
                password.error = "Password length should be greater than or equal to 6"
            }
            else if(UserName.isEmpty()){
                userName.error = "Username is required"
            }
            else if(x == null){
                Toast.makeText(this,"Image is required" , Toast.LENGTH_SHORT).show()
            }
            else
               signUp(Email , Password , UserName)
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent() ,
            ActivityResultCallback {
                image.setImageURI(it)
                uri = it!!
                x = it!!.toString()
            }
        )

        image.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    private fun signUp(email: String, password: String, userName: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadImage(email,userName,mAuth.currentUser?.uid!!)

                } else {
                    // If sign in fails, display a message to the user

                    Toast.makeText(this@SignUpActivity , "Error in signup" , Toast.LENGTH_SHORT).show()

                }
            }
    }


    private fun uploadImage(email: String, userName: String, uid: String) {
        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("profilePic/$filename")
        refStorage.putFile(uri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    addUserToDatabase(email,userName,url,mAuth.currentUser?.uid!!)
                    val intent = Intent(this@SignUpActivity , MainActivity::class.java)
                    finish()
                    startActivity(intent)
                    Log.d("Ironman" , url)
                }
            }
            .addOnFailureListener{

                Toast.makeText(this , "Error in storage" , Toast.LENGTH_SHORT).show()

            }
    }

    private fun addUserToDatabase(email: String, userName: String, url: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("users").child(uid).setValue(User(userName,email,uid,url))
            .addOnFailureListener{
                Toast.makeText(this , it.toString() , Toast.LENGTH_LONG).show()
            }
    }
}