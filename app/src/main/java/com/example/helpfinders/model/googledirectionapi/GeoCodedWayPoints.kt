package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeoCodedWayPoints(
    @Expose
    @SerializedName("geocoder_status")
    var geocoder_status : String? = null,

    @Expose
    @SerializedName("place_id")
    var place_id : String? = null,

    @Expose
    @SerializedName("types")
    var types : MutableList<String> = ArrayList()
    ){
    override fun toString(): String {
        return "GeoCodedWayPoints(geocoder_status=$geocoder_status, place_id=$place_id, types=$types)"
    }
}