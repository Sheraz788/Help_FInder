package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DirectionApiResponse(

    @Expose
    @SerializedName("geocoded_waypoints")
    var geocoded_waypoints : MutableList<GeoCodedWayPoints> = ArrayList(),
    @Expose
    @SerializedName("routes")
    var routes : MutableList<Routes> = ArrayList(),
    @Expose
    @SerializedName("status")
    var status : String? = null
){
    override fun toString(): String {
        return "DirectionApiResponse(geocoded_waypoints=$geocoded_waypoints, routes=$routes, status=$status)"
    }
}