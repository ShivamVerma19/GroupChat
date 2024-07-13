package com.example.groupchat.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.GroupChatActivity
import com.example.groupchat.MessagesActivity
import com.example.groupchat.Modal.User
import com.example.groupchat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context : Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item , parent , false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.textName.text = currentUser.name
        Picasso.get()
            .load(currentUser.url)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context , MessagesActivity::class.java)
            intent.putExtra("userName" , currentUser.name)
            intent.putExtra("userDp" , currentUser.url)
            intent.putExtra("userId" , currentUser.uid)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.userName)
        val image = itemView.findViewById<CircleImageView>(R.id.userProfileImage)
    }
}