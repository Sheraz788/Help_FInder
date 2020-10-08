package com.example.helpfinders.customer.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.example.helpfinders.model.User
import com.example.letsride.R
import kotlinx.android.synthetic.main.confirmation_dialog_layout.*

class ConfirmationDialog (var context : Activity, var user : User) : Dialog(context){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.confirmation_dialog_layout)



        tv_confirm.setOnClickListener {

        }

        tv_cancel.setOnClickListener {
            dismiss()
        }



    }

}