package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Legs(
    @Expose
    @SerializedName("distance")
    var distance : Distance? = null,
    @Expose
    @SerializedName("duration")
    var duration : Duration? = null,
    @Expose
    @SerializedName("end_address")
    var end_address : String? = null,
    @Expose
    @SerializedName("end_location")
    var end_location : EndLocation? = null,
    @Expose
    @SerializedName("start_address")
    var start_address : String? = null,
    @Expose
    @SerializedName("start_location")
    var start_location : StartLocation? = null,
    @Expose
    @SerializedName("steps")
    var steps : MutableList<Steps> = ArrayList(),
    @Expose
    @SerializedName("traffic_speed_entry")
    var traffic_speed_entry : MutableList<String> = ArrayList(),
    @Expose
    @SerializedName("via_waypoint")
    var via_waypoint : MutableList<String> = ArrayList()
){
    override fun toString(): String {
        return "Legs(distance=$distance, " +
                "duration=$duration, " +
                "end_address=$end_address, " +
                "end_location=$end_location, " +
                "start_address=$start_address, " +
                "start_location=$start_location, " +
                "steps=$steps, " +
                "traffic_speed_entry=$traffic_speed_entry, " +
                "via_waypoint=$via_waypoint)"
    }
}