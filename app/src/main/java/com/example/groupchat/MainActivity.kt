package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Adapter.UserAdapter
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import org.checkerframework.checker.units.qual.A

class MainActivity : AppCompatActivity() {
    private lateinit var userRecylerView : RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList : ArrayList<User>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userList = ArrayList()
        userAdapter = UserAdapter(this , userList)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userRecylerView = findViewById(R.id.rcvUser)

        userRecylerView.layoutManager = LinearLayoutManager(this)
        userRecylerView.adapter = userAdapter

        mDbRef.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if(mAuth.currentUser?.uid !=  currentUser?.uid)
                         userList.add(currentUser!!)
                }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){
            mAuth.signOut()
            val intent = Intent(this@MainActivity , LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }

        if(item.itemId == R.id.groupChatMenu){
            val intent = Intent(this@MainActivity , GroupActivity::class.java)
            startActivity(intent)
            return true
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}