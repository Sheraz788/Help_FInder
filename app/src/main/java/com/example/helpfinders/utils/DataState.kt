package com.example.helpfinders.utils

data class DataState<T>(
        val message : String? = null,
        val loading : Boolean = false,
        val data : T? =null
){

    companion object{

        fun <T> error(
            message: String
        ) : DataState<T> {
            return DataState(
                message = message,
                loading = false,
                data = null
            )
        }

        fun <T> loading(
            isLoading : Boolean
        ) : DataState<T> {
            return DataState(
                message = null,
                loading = isLoading,
                data = null
            )
        }

        fun <T> data(
            message: String? = null,
            data : T? = null
        ) : DataState<T> {
            return DataState(
                message = message,
                loading = false,
                data = data
            )
        }

    }


}