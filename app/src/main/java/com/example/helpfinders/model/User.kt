package com.example.helpfinders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var uid : String = "" ,
                var firstName : String = "",
                var lastName : String = "",
                var userEmail : String ="",
                var password: String = "",
                var mobileNumber : String = "",
                var profileImage : String = "",
                var userLocation : String = "",
                var isProvider : Boolean = false,
                var isCustomer : Boolean = false,
                var userstatus : Boolean = false,
                var rememberme : Boolean = false,
                var serviceType : String = "",
                var perHourCharges : Int? = null) : Parcelable{

    override fun toString(): String {
        return "User(uid=$uid, firstName=$firstName, lastName=$lastName, userEmail=$userEmail, password=$password, mobileNumber=$mobileNumber, profileImage=$profileImage, userLocation=$userLocation, isProvider=$isProvider, isCustomer=$isCustomer, userstatus=$userstatus, rememberme=$rememberme, serviceType=$serviceType)"
    }
}