package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Adapter.AddUserAdapter
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddUserActivity : AppCompatActivity() {
    private lateinit var addUserRecylerView : RecyclerView
    private lateinit var addUserAdapter: AddUserAdapter
    private lateinit var remainingUserList : ArrayList<User>
    private lateinit var userList : ArrayList<User>
    private lateinit var addedUserList : ArrayList<User>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private lateinit var createGroupButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        val groupName = intent.getStringExtra("groupName")
        val groupDp = intent.getStringExtra("groupDp")
        val groupId = intent.getStringExtra("groupId")
        val adminId = intent.getStringExtra("adminId")
        remainingUserList = ArrayList()
        userList = ArrayList()
        addedUserList = ArrayList()
        val isAdd = true
        addUserAdapter = AddUserAdapter(this , remainingUserList,groupId,groupDp,groupName,isAdd)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        addUserRecylerView = findViewById(R.id.rcvAddUser)
        createGroupButton = findViewById(R.id.createGroupButton)


        addUserRecylerView.layoutManager = LinearLayoutManager(this)
        addUserRecylerView.adapter = addUserAdapter



        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid !=  currentUser?.uid)
                        userList.add(currentUser!!)
                }

                mDbRef.child("groupToUser").child(groupId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                addedUserList.clear()

                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid !=  currentUser?.uid) {
                        addedUserList.add(currentUser!!)
                    }
                }

                remainingUserList.clear()
                for(user in userList){
                    var isCommon = false
                    for(user2 in addedUserList){
                        if(user2.uid == user.uid) {
                            isCommon = true
                        }
                    }

                    if(!isCommon)
                        remainingUserList.add(user)
                }



                addUserAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )

        createGroupButton.setOnClickListener {
            val intent = Intent(this@AddUserActivity , GroupChatActivity::class.java)
            intent.putExtra("groupName" , groupName)
            intent.putExtra("groupDp" , groupDp)
            intent.putExtra("groupId" , groupId)
            intent.putExtra("adminId" , adminId)
            startActivity(intent)
        }
    }



}