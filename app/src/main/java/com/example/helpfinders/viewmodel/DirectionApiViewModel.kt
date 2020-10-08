package com.example.helpfinders.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.helpfinders.model.googledirectionapi.DirectionApiResponse
import com.example.helpfinders.repository.DirectionAPIRepository
import com.example.helpfinders.state.DirectionApiStateEvent
import com.example.helpfinders.utils.DataState
import com.example.mvilearning.Util.AbsentLiveData

class DirectionApiViewModel : ViewModel(){


    private var _direction_api_state_event : MutableLiveData<DirectionApiStateEvent> = MutableLiveData()

    private var _direction_api_view_state : MutableLiveData<DirectionApiResponse> = MutableLiveData()

    val direction_api_view_state : LiveData<DirectionApiResponse>
        get() = _direction_api_view_state



    var _direction_api_data_state : LiveData<DataState<DirectionApiResponse>> = Transformations
        .switchMap(_direction_api_state_event){ stateEvent->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }


    fun handleStateEvent(directionApiStateEvent: DirectionApiStateEvent) : LiveData<DataState<DirectionApiResponse>> {

        when(directionApiStateEvent){

            is DirectionApiStateEvent.GetDirectionApi -> {
                return DirectionAPIRepository.getDirectionAPIResponse(directionApiStateEvent.origin, directionApiStateEvent.destination, directionApiStateEvent.apikey)
            }

            is DirectionApiStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setDirectionApiResponse(directionApiResponse: DirectionApiResponse){
        val update = getCurrenDirectionApiResponseOrNew()
        update.geocoded_waypoints = directionApiResponse.geocoded_waypoints
        update.routes = directionApiResponse.routes
        update.status = directionApiResponse.status
        _direction_api_view_state.value = update
    }



    fun getCurrenDirectionApiResponseOrNew() : DirectionApiResponse {

        val value = direction_api_view_state.value?.let {
            it
        } ?: DirectionApiResponse()

        return value
    }

    fun setDirectionApiResponseStateEvent(event: DirectionApiStateEvent){
        _direction_api_state_event.value = event
    }




}