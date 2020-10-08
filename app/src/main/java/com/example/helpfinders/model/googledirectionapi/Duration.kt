package com.example.helpfinders.model.googledirectionapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Duration(
    @Expose
    @SerializedName("text")
    var text : String? = null,

    @Expose
    @SerializedName("value")
    var value : Int? = null
){
    override fun toString(): String {
        return "Duration(text=$text, value=$value)"
    }
}