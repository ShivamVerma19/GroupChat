package com.example.groupchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Adapter.MessageAdapter
import com.example.groupchat.Adapter.UserAdapter
import com.example.groupchat.Modal.Message
import com.example.groupchat.Modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessagesActivity : AppCompatActivity() {

    private lateinit var msgRcv : RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var sendBtn : ImageView
    private lateinit var chatName : TextView
    private lateinit var messageProfileImage : CircleImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    var senderRoom : String?= null
    var receiverRoom : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val userName = intent.getStringExtra("userName")
        val userDp = intent.getStringExtra("userDp")
        val rcvId = intent.getStringExtra("userId")
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        val senderId = mAuth.currentUser?.uid!!

        receiverRoom = senderId + rcvId
        senderRoom = rcvId + senderId

        msgRcv = findViewById(R.id.msgRcv)
        messageBox = findViewById(R.id.messageBox)
        sendBtn = findViewById(R.id.sendBtn)
        chatName = findViewById(R.id.chatName)
        messageProfileImage = findViewById(R.id.messageProfileImage)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList,false)

        msgRcv.layoutManager = LinearLayoutManager(this)
        msgRcv.adapter = messageAdapter
        chatName.text = userName
        Picasso.get()
            .load(userDp)
            .into(messageProfileImage);

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
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

        sendBtn.setOnClickListener {
            val message = messageBox.text.toString()

            if (message.isEmpty()) {
                messageBox.error = "Message is required"
            }
            else {
                var currentUserName : String? = null
                mDbRef.child("users").child(senderId).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(User::class.java)
                        currentUserName = currentUser?.name!!

                        val messageObject = Message(message, senderId,currentUserName)

                        mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                            .setValue(messageObject).addOnSuccessListener {
                                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                                    .setValue(messageObject)
                            }

                        messageBox.text.clear()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }

    }
}