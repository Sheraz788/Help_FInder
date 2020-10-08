package com.example.helpfinders.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.helpfinders.customer.ui.home.HomeFragment
import com.example.letsride.R
import com.example.helpfinders.utils.Constants
import com.example.helpfinders.model.User
import com.example.helpfinders.utils.AppPreferencesHandler
import com.example.helpfinders.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference : DatabaseReference
    var rememberme = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Utils.context = this

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        setEventListener()
    }


    fun setEventListener(){
        check_remember.setOnCheckedChangeListener { buttonView, isChecked ->
            rememberme = isChecked
        }
        signup.setOnClickListener {
            val signUpIntent = Intent(this, RegisterActivity::class.java)
            startActivity(signUpIntent)
        }

        login.setOnClickListener {
            Utils.hideSoftKeyboard(this, it)
            if(validateFields()) {
                loginUserAccount()
            }

        }
    }

    fun loginUserAccount(){
        val email = txt_contact_number.text.toString().trim()
        val password = txt_password.text.toString().trim()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                    val uid = it.result?.user?.uid
                    var user : User? = null
                    val firebaseUserRef = firebaseDatabase.getReference("/users/$uid/rememberme")
                    firebaseUserRef.setValue(rememberme)

                    databaseReference = firebaseDatabase.getReference("/users/$uid")
                    databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            user = p0.getValue(
                                User::class.java
                            )
                            storeData(user) //storeData in Preferences
                            if(user!!.isProvider) {
                                val intent = Intent(this@LoginActivity, ProviderMainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }else{
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })


                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    fun storeData(user: User?){

        if (user != null){
            if (check_remember.isChecked){
                AppPreferencesHandler.setUID(user.uid)
                AppPreferencesHandler.setFirstName(user.firstName)
                AppPreferencesHandler.setLastName(user.lastName)
                AppPreferencesHandler.setRememberMe(user.rememberme)
                AppPreferencesHandler.setServiceType(user.serviceType)
                AppPreferencesHandler.setIsCustomer(user.isCustomer)
                AppPreferencesHandler.setIsProvider(user.isProvider)
                AppPreferencesHandler.setProfileImage(user.profileImage)
                AppPreferencesHandler.setPassword(user.password)
                AppPreferencesHandler.setMobileNumber(user.mobileNumber)
                AppPreferencesHandler.setUserEmail(user.userEmail)
                AppPreferencesHandler.setUserLocation(user.userLocation)
                AppPreferencesHandler.setUserStatus(user.userstatus)
            }
        }



    }


// not used
//    fun loginWithContactNumber(){
//        val contactNumber = txt_contact_number.text.toString().trim()
//
//
//        databaseReference.child("users/$contactNumber")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//
//
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    try {
//                        val user: User? = dataSnapshot.getValue(
//                            User::class.java)
//
//                        if (txt_contact_number.text.toString()
//                                .trim() == user?.mobileNumber && txt_password.text.toString()
//                                .trim() == user.password
//                        ) {
//                            val intentMainActivity =
//                                Intent(this@LoginActivity, MainActivity::class.java)
//                            intentMainActivity.putExtra(Constants.USER_KEY, user)
//                            startActivity(intentMainActivity)
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "Logged In Successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "User Does not Exists",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }catch (e : Exception){
//                        Toast.makeText(this@LoginActivity, "Exception: $e", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@LoginActivity, "Error : $error", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            })
//
//    }


    //Implemented to Close Keyboard on touch outside of Edittext Fields
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // all touch events close the keyboard before they are processed except EditText instances.
        // if focus is an EditText we need to check, if the touchevent was inside the focus editTexts
        val currentFocus = currentFocus
        if (currentFocus !is EditText || !isTouchInsideView(ev, currentFocus)) {
            Utils.hideKeyboardFromActivity(this)
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun isTouchInsideView(ev: MotionEvent, currentFocus: View): Boolean {
        val loc = IntArray(2)
        currentFocus.getLocationOnScreen(loc)
        return (ev.rawX > loc[0] && ev.rawY > loc[1] && ev.rawX < loc[0] + currentFocus.width
                && ev.rawY < loc[1] + currentFocus.height)
    }

    fun validateFields() : Boolean{

        val contactNumber = txt_contact_number.text.toString().trim()
        val password = txt_password.text.toString().trim()

        if(contactNumber == ""){
            txt_contact_number.error = "Enter valid email number"
            txt_contact_number.requestFocus()
            return false
        }

        if (password == ""){
            txt_password.error = "Password is required"
            txt_password.requestFocus()
            return false
        }
        return true
    }
}
