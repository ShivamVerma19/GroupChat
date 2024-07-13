package com.example.groupchat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupchat.Modal.Message
import com.example.groupchat.R
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context : Context, val messageList : ArrayList<Message> , val groupActivity : Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 1
    val ITEM_RCV = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view = LayoutInflater.from(context).inflate(R.layout.sent_item , parent , false)
            return SentViewHolder(view)
        }
        else{

            if(groupActivity){
                val view =
                    LayoutInflater.from(context).inflate(R.layout.group_recieve_item, parent, false)
                return ReceiveViewHolder(view)
            }
            else {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.recieve_item, parent, false)
                return ReceiveViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            holder.sentMsg.text = currentMessage.message
        }
        else{
            val viewHolder = holder as ReceiveViewHolder
            holder.rcvMsg.text = currentMessage.message
            if(groupActivity){
                val senderName = holder.itemView.findViewById<TextView>(R.id.senderName)
                senderName.text = currentMessage.senderUserName
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RCV
        }
    }

    class SentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
         val sentMsg = itemView.findViewById<TextView>(R.id.sentTxt)

    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val rcvMsg = itemView.findViewById<TextView>(R.id.receiveTxt)
    }
}