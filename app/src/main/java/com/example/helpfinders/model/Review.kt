package com.example.helpfinders.model

data class Review(
    val rating : Double? = null,
    var comment : String? = null
){
    override fun toString(): String {
        return "Review(rating=$rating, comment=$comment)"
    }
}