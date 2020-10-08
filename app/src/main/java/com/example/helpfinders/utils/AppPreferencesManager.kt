package com.example.helpfinders.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferencesManager {

    companion object {
        val APPLICATION_PREF = "HELP_FINDER"

        fun getApplicationPreferences() : SharedPreferences{
            val sharedPreferences = Utils.context.getSharedPreferences(
                APPLICATION_PREF, Context.MODE_PRIVATE
            )

            return sharedPreferences
        }

        fun getApplicationPreferencesEditor() : SharedPreferences.Editor{
            val preferencesEditor = getSharedPreferencesByKey(APPLICATION_PREF).edit()

            return preferencesEditor
        }

        fun getSharedPreferencesByKey(key : String) : SharedPreferences{
            val sharedPreferences = Utils.context.getSharedPreferences(
                key, Context.MODE_PRIVATE
            )
            return sharedPreferences
        }
    }

}