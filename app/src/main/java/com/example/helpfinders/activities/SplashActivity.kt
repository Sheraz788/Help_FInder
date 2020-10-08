package com.example.helpfinders.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.helpfinders.utils.AppPreferencesHandler
import com.example.helpfinders.utils.Utils
import com.example.letsride.R

class SplashActivity : AppCompatActivity() {
    var defaultTimer = 5000
    var defaultCallBackTimer = 5000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Utils.context = this

        callLoginScreen(defaultTimer, defaultCallBackTimer)
    }


    fun callLoginScreen(timeForWait : Int, callBacKForonTick : Int){
        object : CountDownTimer(timeForWait.toLong(), callBacKForonTick.toLong()) {
            override fun onTick(millisUntilFinished: Long) {} //end of onTick
            override fun onFinish() {
                //here we call Login Screen
                onLoginSuccess()
            } //end of onFinsih
        }.start()


    }

    fun onLoginSuccess(){

        if (AppPreferencesHandler.getUID() != null){
            if(AppPreferencesHandler.getIsProvider()) {
                val intent = Intent(this@SplashActivity, ProviderMainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }else{
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }





    }
}