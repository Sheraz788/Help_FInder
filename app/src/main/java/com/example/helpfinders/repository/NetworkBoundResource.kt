package com.example.helpfinders.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.helpfinders.utils.*
import kotlinx.coroutines.*

abstract class NetworkBoundResource <ResponseObject, viewStateType> {

    protected val result = MediatorLiveData<DataState<viewStateType>>()


    init {
        result.value = DataState.loading(true)

        GlobalScope.launch(Dispatchers.IO) {
            delay(5000)

            withContext(Dispatchers.Main){
                val apiResponse = createCall()

                result.addSource(apiResponse){response->
                    result.removeSource(apiResponse)
                    handleNetworkCall(response)
                }

            }

        }
    }

    fun handleNetworkCall(response : GenericApiResponse<ResponseObject>){
        when(response){

            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                println("DEBUG: NetworkBoundResource: ${response.errorMessage}")
                onReturnError(response.errorMessage)
            }

            is ApiEmptyResponse -> {
                println("DEBUG: NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onReturnError("HTTP 204. Returned NOTHING.")
            }

        }
    }

    fun onReturnError(message : String){
        result.value = DataState.error(message)
    }
    abstract fun handleApiSuccessResponse(response : ApiSuccessResponse<ResponseObject>)

    abstract fun createCall() : LiveData<GenericApiResponse<ResponseObject>>

    fun asLiveData()= result as LiveData<DataState<viewStateType>>
}