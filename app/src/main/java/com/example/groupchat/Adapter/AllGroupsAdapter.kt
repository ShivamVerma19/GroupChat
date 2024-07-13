package com.example.groupchat.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.AllGroupsActivity
import com.example.groupchat.GroupActivity
import com.example.groupchat.GroupChatActivity
import com.example.groupchat.Modal.GroupModal
import com.example.groupchat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AllGroupsAdapter(val context : Context, val groupList: ArrayList<GroupModal>) : RecyclerView.Adapter<AllGroupsAdapter.AllGroupsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllGroupsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item , parent , false)
        return AllGroupsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: AllGroupsViewHolder, position: Int) {
        val currentGroup = groupList[position]

        holder.textName.text = currentGroup.groupName
        Picasso.get()
            .load(currentGroup.groupUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context , GroupChatActivity::class.java)
            intent.putExtra("groupName" , currentGroup.groupName)
            intent.putExtra("groupDp" , currentGroup.groupUrl)
            intent.putExtra("groupId" , currentGroup.groupId)
            intent.putExtra("adminId" , currentGroup.adminId)
            context.startActivity(intent)
        }
    }

    class AllGroupsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.userName)
        val image = itemView.findViewById<CircleImageView>(R.id.userProfileImage)
    }
}