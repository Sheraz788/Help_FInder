package com.example.helpfinders.state

sealed class DirectionApiStateEvent {

    class GetDirectionApi(var origin : String, var destination : String, var apikey : String) : DirectionApiStateEvent()

    class None : DirectionApiStateEvent()

}