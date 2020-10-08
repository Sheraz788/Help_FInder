package com.example.helpfinders.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils{
    companion object{
        lateinit var context: Activity
        fun fromMillisToTimeString(millis: Long) : String {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(millis)
        }

        fun hideKeyboardFromActivity(context: Activity) {
            // TODO Auto-generated method stub
            val im = context
                .applicationContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
            im.hideSoftInputFromWindow(
                context.window.decorView
                    .windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        fun hideSoftKeyboard(activity: Activity, view: View) {
            val imm = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }

        fun getDateTimeFormat(): String? {
            return "EEE MMM dd HH:mm:ss Z yyyy"
        }

        fun getTime24HoursFormat(stringDate: String?): String? {
            try {
                val displayFormat =
                    SimpleDateFormat("yyyy-MM-dd hh:mm aa")
                val hr12 = displayFormat.parse(stringDate)
                val parseFormat =
                    SimpleDateFormat("yyyy-MM-dd HH:mm")
                return parseFormat.format(hr12)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }
    }
}