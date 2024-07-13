package com.example.groupchat.Modal

class Message {
    var message : String? = null
    var senderId : String? = null
    var senderUserName : String? = null

    constructor(){}

    constructor(message: String? , senderId: String? , senderUserName : String?){
        this.message = message
        this.senderId = senderId
        this.senderUserName = senderUserName
    }

}