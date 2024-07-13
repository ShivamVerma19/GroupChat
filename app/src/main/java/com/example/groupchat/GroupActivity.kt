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
import com.example.groupchat.Modal.GroupModal
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID

class GroupActivity : AppCompatActivity() {

    private lateinit var groupNameTxt : EditText
    private lateinit var groupProfileImage : CircleImageView
    private lateinit var createGroupButton : Button
    private lateinit var viewGroupButton : Button
    private lateinit var uri : Uri
    private  var x : String? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        createGroupButton = findViewById(R.id.createGroupButton)
        viewGroupButton = findViewById(R.id.viewGroupButton)
        groupNameTxt = findViewById(R.id.groupNameTxt)
        groupProfileImage = findViewById(R.id.groupProfileImage)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()



        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent() ,
            ActivityResultCallback {
                groupProfileImage.setImageURI(it)
                uri = it!!
                x = it!!.toString()
            }
        )

        groupProfileImage.setOnClickListener {
            galleryImage.launch("image/*")
        }

        createGroupButton.setOnClickListener {
            val groupName = groupNameTxt.text.toString()
            if(groupName.isEmpty()){
                  groupNameTxt.error = "Groupname is required"
            }
            else if(x == null){
                  Toast.makeText(this,"Image is required" , Toast.LENGTH_SHORT).show()
            }
            else
               uploadImage(groupName)
        }

        viewGroupButton.setOnClickListener {
            val intent = Intent(this@GroupActivity , AllGroupsActivity::class.java)
            startActivity(intent)
        }
    }



    private fun uploadImage(groupName: String) {
        val filename = UUID.randomUUID().toString() + ".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("groupProfilePic/$filename")
        refStorage.putFile(uri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    addGroupToDatabase(groupName,url,mAuth.currentUser?.uid!!)
                }
            }
            .addOnFailureListener{

                Toast.makeText(this , "Error in storage" , Toast.LENGTH_SHORT).show()

            }
    }

    private fun addGroupToDatabase(groupName: String, url: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        val groupId = UUID.randomUUID().toString()
        mDbRef.child("groups").child(groupId).setValue(GroupModal(groupName,groupId,uid,url))
            .addOnFailureListener{
                Toast.makeText(this , it.toString() , Toast.LENGTH_LONG).show()
            }


        //getting current user Detail
        val userRef = mDbRef.child("users").child(mAuth.currentUser?.uid!!)

        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val userDetails = dataSnapshot.getValue(User::class.java)
                mDbRef.child("groupToUser").child(groupId).child(uid).setValue(userDetails)
                // Use the userDetails object
            } else {
                Log.e("MainActivity", "User not found")
            }
        }.addOnFailureListener { exception ->
            Log.e("Xyz", "Error getting user details", exception)
        }



        mDbRef.child("UserToGroup").child(uid).child(groupId).setValue(GroupModal(groupName,groupId,uid,url))
        mDbRef.child("GroupAdmins").child(groupId).child(uid).setValue(uid)

        val intent = Intent(this@GroupActivity , GroupChatActivity::class.java)
        intent.putExtra("groupName" , groupName)
        intent.putExtra("groupDp" , url)
        intent.putExtra("groupId" , groupId)
        intent.putExtra("adminId" , uid)
        finish()
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@GroupActivity , MainActivity::class.java)
        startActivity(intent)
    }
}