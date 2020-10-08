package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @Expose
    @SerializedName("points")
    var points : String? = null
){
    override fun toString(): String {
        return "OverviewPolyline(points=$points)"
    }
}