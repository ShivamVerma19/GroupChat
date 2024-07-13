package com.example.groupchat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Modal.GroupModal
import com.example.groupchat.Modal.User
import com.example.groupchat.R
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AddUserAdapter(
    val context: Context,
    val userList: ArrayList<User>,
    val groupId: String?,
    val groupDp: String?,
    val groupName: String?,
    val isAdd: Boolean,
    ) : RecyclerView.Adapter<AddUserAdapter.AddUserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.add_user_item , parent , false)
        return AddUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AddUserViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.textName.text = currentUser.name
        Picasso.get()
            .load(currentUser.url)
            .into(holder.image)

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if(isAdd)
                   addUser(currentUser.uid,currentUser)
                else
                    removeUser(currentUser.uid,currentUser)
            } else {

            }
        }
    }

    private fun removeUser(uid: String?, currentUser: User) {
        val mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("groupToUser").child(groupId!!).child(uid!!).removeValue()
        mDbRef.child("UserToGroup").child(uid).child(groupId).removeValue()
    }

    private fun addUser(uid: String?, currentUser: User) {
        val mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("groupToUser").child(groupId!!).child(uid!!).setValue(currentUser)
        mDbRef.child("UserToGroup").child(uid).child(groupId).setValue(GroupModal(groupName,groupId,uid,groupDp))
    }

    class AddUserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.addUserserName)
        val image = itemView.findViewById<CircleImageView>(R.id.addUserProfileImage)
        val checkBox = itemView.findViewById<CheckBox>(R.id.addUserCheckBox)
    }
}