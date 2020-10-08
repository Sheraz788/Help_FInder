package com.example.helpfinders.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.letsride.R
import com.example.helpfinders.utils.Constants
import com.example.helpfinders.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_phone_verification.*
import java.util.concurrent.TimeUnit

class PhoneVerificationActivity : AppCompatActivity() {

    var userIntent : User? = null
    lateinit var verificationID : String
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var mAuth : FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        userIntent = intent.getParcelableExtra<User>(
            Constants.USER_KEY)
        sendVerificationCode(userIntent?.mobileNumber)

        verify_phone_btn.setOnClickListener {
            if(txt_verification_code.text.toString().trim() != ""){
                verifyReceivedOTP(txt_verification_code.text.toString().trim())
            }else{
                Toast.makeText(this, "Please enter verification code", Toast.LENGTH_SHORT).show()
            }
        }

        resend_otp.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            var phoneNumber = "+92${userIntent?.mobileNumber}"
            try {
                if(resendToken !=null) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        verificationCodeCallBack,
                        resendToken
                    )
                }else{
                    Toast.makeText(this, "Please wait ...", Toast.LENGTH_LONG).show()
                }
            }catch (e : Exception){
                Toast.makeText(this, "$e", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun sendVerificationCode(mobileNumber : String?){
        progressbar.visibility = View.VISIBLE
        var phoneNumber = "+92$mobileNumber"
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            verificationCodeCallBack
        )
    }

    val verificationCodeCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)

            println("ONCODE SENT : $verificationId $token")
            verificationID = verificationId!!
            resendToken = token
            progressbar.visibility = View.GONE
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            progressbar.visibility = View.GONE
            //SMS Code
            var smsCode = credential?.smsCode

            //may be sms code is not detected automatically
            if (smsCode != null){
                txt_verification_code.setText(smsCode)

                verifyReceivedOTP(smsCode)
            }else{
                Toast.makeText(this@PhoneVerificationActivity, "Not Getting Verification Code", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onVerificationFailed(exception: FirebaseException) {
            println("EXCEPION: $exception")

            if(exception is FirebaseAuthInvalidCredentialsException){
                println("FirebaseAuthInvalidCredentialsException: $exception")
            }else if(exception is FirebaseTooManyRequestsException){
                println("FirebaseTooManyRequestsException: $exception")
                Toast.makeText(this@PhoneVerificationActivity, "$exception", Toast.LENGTH_SHORT).show()
            }

        }


    }

    fun verifyReceivedOTP(code : String?){
        val credential = PhoneAuthProvider.getCredential(verificationID, code!!)

        signInWithPhoneAuthCredential(credential)
    }

    fun signInWithPhoneAuthCredential(credential : PhoneAuthCredential) {


        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {task: Task<AuthResult> ->
                if(task.isSuccessful){
                    var uid = mAuth.uid ?: ""
                    userIntent?.uid = uid
                    databaseReference.child("users/$uid").setValue(userIntent)
                    Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()

                    var loginIntent = Intent(this, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginIntent)

                }else{
                    var errorMessage = "Something went wrong, we will fix it soon"

                    if(task.exception is FirebaseAuthInvalidCredentialsException){
                        errorMessage = "Invalid Code entered ..."
                        Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }



}
