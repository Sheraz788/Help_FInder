package com.example.helpfinders.model

data class Notification(
    var uid : String? = null,
    var fromId : String? = null,
    var type : String? = null,
    var viewstatus : Boolean? = null,
    var timestamp : Long? = null,
    var providerstatus : String? = null,
    var customerstatus : String? = null,
    var starttime : Long? = null,
    var endtime : Long? = null,
    var servicename : String? = null,
    var serviceprice : Int? = null){
    override fun toString(): String {
        return "Notification(uid=$uid, " +
                "fromId=$fromId, " +
                "type=$type, " +
                "viewstatus=$viewstatus, " +
                "timestamp=$timestamp, " +
                "providerstatus=$providerstatus, " +
                "customerstatus=$customerstatus, " +
                "starttime=$starttime, " +
                "endtime=$endtime" +
                ")"
    }
}