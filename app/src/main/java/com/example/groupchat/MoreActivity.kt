package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class MoreActivity : AppCompatActivity() {

    private lateinit var leaveBtn : Button
    private lateinit var makeAdmBtn : Button
    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var adminList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        leaveBtn = findViewById(R.id.leaveBtn)
        makeAdmBtn = findViewById(R.id.makeAdmBtn)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        val uid = mAuth.currentUser?.uid!!
        val groupName = intent.getStringExtra("groupName")
        val groupDp = intent.getStringExtra("groupDp")
        val groupId = intent.getStringExtra("groupId")
        val adminId = intent.getStringExtra("adminId")
        adminList = ArrayList()

        leaveBtn.setOnClickListener {

            mDbRef.child("groupToUser").child(groupId!!).child(uid).removeValue()
            mDbRef.child("UserToGroup").child(uid).child(groupId).removeValue()

            if(uid == adminId){
                mDbRef.child("groupToUser").child(groupId!!).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for(postSnapshot in snapshot.children){
                            val currentUser =  postSnapshot.getValue(User::class.java)
                            mDbRef.child("GroupAdmins").child(groupId).child(currentUser?.uid!!).setValue(currentUser?.uid!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

            val intent = Intent(this@MoreActivity , AllGroupsActivity::class.java)
            startActivity(intent)
        }
    }
}