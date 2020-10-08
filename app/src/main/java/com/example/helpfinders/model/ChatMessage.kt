package com.example.helpfinders.model

data class ChatMessage(var id : String = "", var message : String = "", var fromId: String = "", var toId : String = "", var isSending : Boolean = false, var isReceiving : Boolean = false, var timestamp : Long = -1){
    override fun toString(): String {
        return "ChatMessage(id=$id, message=$message, fromId=$fromId, toId=$toId, isSending=$isSending, isReceiving=$isReceiving, timeStamp=$timestamp)"
    }
}