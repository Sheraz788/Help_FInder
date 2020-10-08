package com.example.helpfinders.repository

import androidx.lifecycle.LiveData
import com.example.helpfinders.model.googledirectionapi.DirectionApiResponse
import com.example.helpfinders.utils.ApiSuccessResponse
import com.example.helpfinders.utils.DataState
import com.example.helpfinders.utils.GenericApiResponse
import com.example.helpfinders.webservices.MyRetrofitBuilder

object DirectionAPIRepository {

    //get direction api response
    fun getDirectionAPIResponse(origin : String, destination : String, apikey : String) : LiveData<DataState<DirectionApiResponse>> {

        return object : NetworkBoundResource<DirectionApiResponse, DirectionApiResponse>(){
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<DirectionApiResponse>) {
                result.value = DataState.data(
                    null,
                    data = response.body
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<DirectionApiResponse>> {
                return MyRetrofitBuilder.apiService.getDirectionAPIResponse(origin, destination, apikey)
            }

        }.asLiveData()

    }


}