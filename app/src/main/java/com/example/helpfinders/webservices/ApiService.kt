package com.example.helpfinders.webservices

import androidx.lifecycle.LiveData
import com.example.helpfinders.model.googledirectionapi.DirectionApiResponse
import com.example.helpfinders.utils.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("directions/json")
    fun getDirectionAPIResponse(@Query("origin") origin : String, @Query("destination") destination : String, @Query("key") key : String) : LiveData<GenericApiResponse<DirectionApiResponse>>

}