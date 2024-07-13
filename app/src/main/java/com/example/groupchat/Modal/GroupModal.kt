package com.example.groupchat.Modal

class GroupModal {

    var groupName: String? = null
    var groupId: String? = null
    var adminId:String? = null
    var groupUrl:String? = null

    constructor(){}

    constructor(groupName:String? , groupId:String? , adminId:String?,groupUrl:String?){
        this.groupName = groupName
        this.groupId = groupId
        this.adminId = adminId
        this.groupUrl = groupUrl
    }

}