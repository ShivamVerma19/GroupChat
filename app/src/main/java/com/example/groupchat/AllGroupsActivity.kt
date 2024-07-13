package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Adapter.AllGroupsAdapter
import com.example.groupchat.Adapter.UserAdapter
import com.example.groupchat.Modal.GroupModal
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllGroupsActivity : AppCompatActivity() {
    private lateinit var userRecylerView : RecyclerView
    private lateinit var userAdapter: AllGroupsAdapter
    private lateinit var groupList : ArrayList<GroupModal>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_groups)

        groupList = ArrayList()
        userAdapter = AllGroupsAdapter(this , groupList)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userRecylerView = findViewById(R.id.rcvAllGroups)

        userRecylerView.layoutManager = LinearLayoutManager(this)
        userRecylerView.adapter = userAdapter

        mDbRef.child("UserToGroup").child(mAuth.currentUser?.uid!!).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                groupList.clear()

                for(postSnapshot in snapshot.children){
                    val currentGroup = postSnapshot.getValue(GroupModal::class.java)
                    groupList.add(currentGroup!!)
                }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@AllGroupsActivity , GroupActivity::class.java)
        startActivity(intent)
    }
}