package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Bounds(
    @Expose
    @SerializedName("northwest")
    var northwest : NorthWest? = null,
    @Expose
    @SerializedName("southwest")
    var southwest : SouthWest? = null
){
    override fun toString(): String {
        return "Bounds(northwest=$northwest, southwest=$southwest)"
    }
}