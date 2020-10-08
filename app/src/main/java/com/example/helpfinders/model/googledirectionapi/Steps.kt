package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Steps(
    @Expose
    @SerializedName("distance")
    var distance : Distance? = null,
    @Expose
    @SerializedName("duration")
    var duration : Duration? = null,
    @Expose
    @SerializedName("end_location")
    var end_location : EndLocation? = null,
    @Expose
    @SerializedName("html_instructions")
    var html_instructions : String? = null,
    @Expose
    @SerializedName("polyline")
    var polyline : OverviewPolyline? = null,
    @Expose
    @SerializedName("start_location")
    var start_location : StartLocation? = null,
    @Expose
    @SerializedName("travel_mode")
    var travel_mode : String? = null
){
    override fun toString(): String {
        return "Steps(distance=$distance, " +
                "duration=$duration, " +
                "end_location=$end_location, " +
                "html_instructions=$html_instructions, " +
                "polyline=$polyline, " +
                "start_location=$start_location, " +
                "travel_mode=$travel_mode)"
    }
}