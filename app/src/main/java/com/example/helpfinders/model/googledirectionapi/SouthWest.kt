package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SouthWest(
    @Expose
    @SerializedName("lat")
    var lat : Double? = null,
    @Expose
    @SerializedName("lng")
    var lng : Double? = null
){
    override fun toString(): String {
        return "SouthWest(lat=$lat, lng=$lng)"
    }
}