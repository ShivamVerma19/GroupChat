package com.example.groupchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Adapter.MessageAdapter
import com.example.groupchat.Modal.Message
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class GroupChatActivity : AppCompatActivity() {
    private lateinit var gcName : TextView
    private lateinit var gcProfileImage : CircleImageView
    private lateinit var addUserButton : Button
    private lateinit var removeUserButton : Button
    private lateinit var deleteGroup : Button
    private lateinit var groupMessageBox : EditText
    private lateinit var groupSendBtn : ImageView
    private lateinit var mDbRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth
    private lateinit var groupMsgRcv : RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var adminList : ArrayList<String>
    private lateinit var gcLeave : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        gcName = findViewById(R.id.gcName)
        gcProfileImage = findViewById(R.id.gcProfileImage)
        addUserButton = findViewById(R.id.addUserButton)
        removeUserButton = findViewById(R.id.removeUserButton)
        groupMessageBox = findViewById(R.id.groupMessageBox)
        groupSendBtn = findViewById(R.id.groupSendBtn)
        groupMsgRcv = findViewById(R.id.groupMsgRcv)
        deleteGroup = findViewById(R.id.deleteGroup)
        gcLeave = findViewById(R.id.gcLeave)
        mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid!!
        mDbRef = FirebaseDatabase.getInstance().getReference()
        messageList = ArrayList()
        adminList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList,true)

        val groupName = intent.getStringExtra("groupName")
        val groupDp = intent.getStringExtra("groupDp")
        val groupId = intent.getStringExtra("groupId")
        val adminId = intent.getStringExtra("adminId")

        groupMsgRcv.layoutManager = LinearLayoutManager(this)
        groupMsgRcv.adapter = messageAdapter

        Log.d("gN" , groupName!!)
        gcName.text = groupName
        Picasso.get()
            .load(groupDp)
            .into(gcProfileImage)

        gcLeave.setOnClickListener {
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

            val intent = Intent(this@GroupChatActivity , AllGroupsActivity::class.java)
            finish()
            startActivity(intent)
        }


        mDbRef.child("groupChats").child(groupId!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        addUserButton.setOnClickListener {
            getAdminList(groupId,groupName,groupDp,adminId)
        }

        removeUserButton.setOnClickListener {
            getAdminListForRemove(groupId,groupName,groupDp,adminId)
        }

        groupSendBtn.setOnClickListener {
            val message = groupMessageBox.text.toString()
            if (message.isEmpty()) {
                groupMessageBox.error = "Message is required"
            }
            else {

                var currentUserName : String? = null
                mDbRef.child("users").child( mAuth.currentUser?.uid!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                            val currentUser = snapshot.getValue(User::class.java)
                            currentUserName = currentUser?.name!!


                            val messageObject = Message(message, mAuth.currentUser?.uid!! , currentUserName)


                           mDbRef.child("groupChats").child(groupId!!).child("messages").push()
                            .setValue(messageObject)

                           groupMessageBox.text.clear()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }

        deleteGroup.setOnClickListener {

            getAdminListForDelete(groupId,groupName,groupDp)

        }
    }

    private fun getAdminListForDelete(groupId: String, groupName: String, groupDp: String?) {
        mDbRef.child("GroupAdmins").child(groupId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (adminSnapshot in snapshot.children) {
                    val adminId = adminSnapshot.key // or adminSnapshot.getValue(String::class.java)
                    adminId?.let {
                        adminList.add(it)
                    }
                }
                // Now you have the list of adminIds
                var isAdmin = false
                for(userId in adminList) {
                    if (userId == mAuth.currentUser?.uid!!) {
                        isAdmin = true
                    }
                }
                    if(isAdmin == false){
                        Toast.makeText(this@GroupChatActivity , "You are not admin" , Toast.LENGTH_SHORT).show()
                    }
                    else{
                        mDbRef.child("groupToUser").child(groupId).addValueEventListener(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {

                                for(postSnapshot in snapshot.children){
                                    val currentUser = postSnapshot.getValue(User::class.java)
                                    mDbRef.child("UserToGroup").child(currentUser?.uid!!).child(groupId).removeValue()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        }
                        )

                        mDbRef.child("GroupAdmins").child(groupId).removeValue()
                        mDbRef.child("groupToUser").child(groupId).removeValue()
                        mDbRef.child("groupChats").child(groupId).removeValue()
                        mDbRef.child("groups").child(groupId).removeValue()

                        val intent = Intent(this@GroupChatActivity , AllGroupsActivity::class.java)
                        startActivity(intent)
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun getAdminListForRemove(
        groupId: String,
        groupName: String,
        groupDp: String?,
        adminId: String?
    ) {
        mDbRef.child("GroupAdmins").child(groupId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (adminSnapshot in snapshot.children) {
                    val adminId = adminSnapshot.key // or adminSnapshot.getValue(String::class.java)
                    adminId?.let {
                        adminList.add(it)
                    }
                }
                // Now you have the list of adminIds
                var isAdmin = false
                for(userId in adminList) {
                    if (userId == mAuth.currentUser?.uid!!) {
                        isAdmin = true
                    }
                }
                    if(isAdmin == false){
                        Toast.makeText(this@GroupChatActivity , "You are not admin" , Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent = Intent(this@GroupChatActivity , RemoveUserActivity::class.java)
                        intent.putExtra("groupName" , groupName)
                        intent.putExtra("groupDp" , groupDp)
                        intent.putExtra("groupId" , groupId)
                        intent.putExtra("adminId" , adminId)
                        startActivity(intent)
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getAdminList(groupId: String, groupName: String, groupDp: String?, adminId: String?) {
        mDbRef.child("GroupAdmins").child(groupId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (adminSnapshot in snapshot.children) {
                    val adminId = adminSnapshot.key // or adminSnapshot.getValue(String::class.java)
                    adminId?.let {
                        adminList.add(it)
                    }
                }
                // Now you have the list of adminIds
                var isAdmin = false
                for(userId in adminList) {
                    if (userId == mAuth.currentUser?.uid!!) {
                        isAdmin = true
                    }
                }
                    if(isAdmin == false){
                        Toast.makeText(this@GroupChatActivity , "You are not admin" , Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent = Intent(this@GroupChatActivity , AddUserActivity::class.java)
                        intent.putExtra("groupName" , groupName)
                        intent.putExtra("groupDp" , groupDp)
                        intent.putExtra("groupId" , groupId)
                        intent.putExtra("adminId" , adminId)
                        startActivity(intent)
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@GroupChatActivity , AllGroupsActivity::class.java)
        startActivity(intent)
    }
}