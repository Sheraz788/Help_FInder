package com.example.helpfinders.provider.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.User
import com.example.helpfinders.utils.Utils
import com.example.letsride.R
import kotlinx.android.synthetic.main.payment_receipt_dialog_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class PaymentReceiptDialog(var context : Activity, var notification: Notification, var user: User) : Dialog(context){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.payment_receipt_dialog_layout)

        var totalPrice = 0

        var calendar = Calendar.getInstance()
        calendar.timeInMillis = notification.starttime!!

        val simpleDateFormat =
            SimpleDateFormat(Utils.getDateTimeFormat(), Locale.ENGLISH)
        var dateStartTime: Date? = null
        try {
            dateStartTime = simpleDateFormat.parse(calendar.time.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }


//        val startTime = Utils.getTime24HoursFormat(dateStartTime.toString())

        val startTimeSplit: Array<String> = dateStartTime.toString().split("\\s+".toRegex()).toTypedArray()
        val serviceStartTimeSplit: Array<String> = startTimeSplit[3].split(":".toRegex()).toTypedArray()


        calendar.timeInMillis = notification.endtime!!

        val simpleEndDateFormat =
            SimpleDateFormat(Utils.getDateTimeFormat(), Locale.ENGLISH)
        var dateEndTime: Date? = null
        try {
            dateEndTime = simpleEndDateFormat.parse(calendar.time.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
//        val endTime = Utils.getTime24HoursFormat(dateEndTime.toString())

        val endTimeSplit: Array<String> = dateEndTime.toString().split("\\s+".toRegex()).toTypedArray()
        val serviceEndTimeSplit: Array<String> = endTimeSplit[3].split(":".toRegex()).toTypedArray()


        val totalHourTimeSpend = serviceEndTimeSplit[0].toInt().minus(serviceStartTimeSplit[0].toInt())

        if (totalHourTimeSpend == 0){
            totalPrice = notification.serviceprice!!
        }else{
            if (user.perHourCharges != null) {
                totalPrice =
                    (totalHourTimeSpend * user.perHourCharges!!) + notification.serviceprice!!
            }else{
                totalPrice = notification.serviceprice!!
            }
        }


        tv_wallet_charge.text = "${totalPrice.toString()} Rs."
        tv_wallet_total.text = "${totalPrice.toString()} Rs."

        tv_ok.setOnClickListener {
            dismiss()
        }

    }



    fun convertTimeInMillisToHrMin(timeInMillis : Long) : String{
        val hours: Long = TimeUnit.MILLISECONDS.toHours(timeInMillis)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeInMillis)

        return String.format("%d:%02d:%02d", hours,minutes,seconds)
    }


}