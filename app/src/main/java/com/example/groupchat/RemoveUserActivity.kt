package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class RemoveUserActivity : AppCompatActivity() {

    private lateinit var removeUserRecylerView : RecyclerView
    private lateinit var addUserAdapter: AddUserAdapter
    private lateinit var addUserList : ArrayList<User>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private lateinit var removeDoneBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_user)

        val groupName = intent.getStringExtra("groupName")
        val groupDp = intent.getStringExtra("groupDp")
        val groupId = intent.getStringExtra("groupId")
        val adminId = intent.getStringExtra("adminId")
        addUserList = ArrayList()
        val isAdd = false
        addUserAdapter = AddUserAdapter(this, addUserList, groupId, groupDp, groupName, isAdd)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        removeUserRecylerView = findViewById(R.id.rcvRemoveUser)
        removeDoneBtn = findViewById(R.id.removeDoneBtn)

        removeUserRecylerView.layoutManager = LinearLayoutManager(this)
        removeUserRecylerView.adapter = addUserAdapter

        mDbRef.child("groupToUser").child(groupId!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                addUserList.clear()

                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid !=  currentUser?.uid)
                        addUserList.add(currentUser!!)
                }

                addUserAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )

        removeDoneBtn.setOnClickListener {
            val intent = Intent(this@RemoveUserActivity , GroupChatActivity::class.java)
            intent.putExtra("groupName" , groupName)
            intent.putExtra("groupDp" , groupDp)
            intent.putExtra("groupId" , groupId)
            intent.putExtra("adminId" , adminId)
            startActivity(intent)
        }

    }


}