package com.example.groupchat.Modal

class User {
    var name: String? = null
    var email: String? = null
    var uid:String? = null
    var url:String? = null

    constructor(){}

    constructor(name:String? , email:String? , uid:String?,url:String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.url = url
    }

}