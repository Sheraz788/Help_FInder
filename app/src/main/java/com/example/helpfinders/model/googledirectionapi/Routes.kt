package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Routes(
    @Expose
    @SerializedName("bounds")
    var bounds : Bounds? = null,
    @Expose
    @SerializedName("copyrights")
    var copyrights : String? = null,
    @Expose
    @SerializedName("legs")
    var legs : MutableList<Legs> = ArrayList(),
    @Expose
    @SerializedName("overview_polyline")
    var overview_polyline : OverviewPolyline? = null,
    @Expose
    @SerializedName("summary")
    var summary : String? = null,
    @Expose
    @SerializedName("warnings")
    var warnings: MutableList<String> = ArrayList(),
    @Expose
    @SerializedName("waypoint_order")
    var waypoint_order: MutableList<String> = ArrayList()
){
    override fun toString(): String {
        return "Routes(bounds=$bounds, " +
                "copyrights=$copyrights, " +
                "legs=$legs, " +
                "overview_polyline=$overview_polyline, " +
                "summary=$summary, " +
                "warnings=$warnings, " +
                "waypoint_order=$waypoint_order)"
    }
}