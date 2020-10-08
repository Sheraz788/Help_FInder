package com.example.helpfinders.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.letsride.R
import com.example.helpfinders.model.User
import com.example.helpfinders.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var progressDialog : ProgressBar
    lateinit var servicesOptions : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        initResources()
        setEventListeners()
        setAdapter()
    }

    fun initResources(){
        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)

    }

    fun setEventListeners(){

        checkbox_service_provider.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                service_dropdown.visibility = View.VISIBLE
            }else{
                service_dropdown.visibility = View.GONE
            }
        }

        txt_password.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                when {
                    s?.isEmpty()!! -> {
                        tv_password_strength.text = getString(R.string.password_not_entered)
                        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.progress = 0
                    }
                    s.length < 6 -> {
                        tv_password_strength.text = getString(R.string.weak_password)
                        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.weak_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.weak_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.progress = 20
                    }
                    s.length < 10 -> {
                        tv_password_strength.text = getString(R.string.medium_password)
                        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.medium_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.medium_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.progress = 40
                    }
                    s.length < 15 -> {
                        tv_password_strength.text = getString(R.string.strong_password)
                        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.strong_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.strong_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.progress = 70
                    }
                    else -> {
                        tv_password_strength.text = getString(R.string.very_strong_password)
                        password_strength_seekbar.progressDrawable.setColorFilter(resources.getColor(R.color.very_strong_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.thumb.setColorFilter(resources.getColor(R.color.very_strong_password), PorterDuff.Mode.SRC_IN)
                        password_strength_seekbar.progress = 100
                    }
                };
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        user_signup_btn.setOnClickListener {
            Utils.hideSoftKeyboard(this, it)
            if(validatesFields()){

                progress_bar.visibility = View.VISIBLE
                user_signup_btn.isEnabled = false
                user_signup_btn.text = ""
                registerUser()
            }
        }

        tv_login.setOnClickListener {

        }
    }

    private fun setAdapter(){
        servicesOptions = ArrayList()
        servicesOptions.add("Select Service")
        servicesOptions.add("Plumber")
        servicesOptions.add("Electrical")
        servicesOptions.add("Painting")
        servicesOptions.add("Salon At Home")
        servicesOptions.add("Carpentary")
        servicesOptions.add("Home Cleaning")

        val arrayAdapter = object : ArrayAdapter<String>(this, R.layout.layout_custom_dropdown, servicesOptions) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?,
                                         parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(context.resources.getColor(R.color.text_color))
                }
                return view
            }
        }
        service_dropdown.adapter = arrayAdapter


    }

    private fun registerUser(){

        val firstName = txt_first_name.text.toString().trim()
        val lastName = txt_last_name.text.toString().trim()
        val email = txt_email.text.toString().trim()
        val password = txt_password.text.toString().trim()
        val contactNumber = txt_contact_number.text.toString().trim()
        val userLocation = txt_location.text.toString().trim()
        var isProvider = false
        var isCustomer = false
        var serviceType = ""

        if(checkbox_service_provider.isChecked){
            serviceType = service_dropdown.selectedItem.toString()
            isProvider = true
        }else{
            serviceType = ""
            isCustomer = true
        }



        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var uid = FirebaseAuth.getInstance().uid ?: ""
                    Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show()
                    var user = User(
                        uid,
                        firstName,
                        lastName,
                        email,
                        password,
                        contactNumber,
                        "",
                        userLocation,
                        isProvider,
                        isCustomer,
                        false,
                        false,
                        serviceType
                    )
                    saveUserInformation(user)
                }
            }

            .addOnFailureListener {
                progress_bar.visibility = View.GONE
                user_signup_btn.isEnabled = true
                user_signup_btn.text = this.getString(R.string.register)
                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }


    private fun saveUserInformation(user: User){

        var uid = FirebaseAuth.getInstance().uid ?: ""

        databaseReference.child("users/$uid").setValue(user)
            .addOnSuccessListener {
                progress_bar.visibility = View.GONE
                user_signup_btn.isEnabled = true
                user_signup_btn.text = this.getString(R.string.register)
                val intent = Intent(this, LoginActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                progress_bar.visibility = View.GONE
                user_signup_btn.isEnabled = true
                user_signup_btn.text = this.getString(R.string.register)
                Toast.makeText(this, "Failed to save user: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

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


    fun isTouchInsideView(ev: MotionEvent, currentFocus: View): Boolean {
        val loc = IntArray(2)
        currentFocus.getLocationOnScreen(loc)
        return (ev.rawX > loc[0] && ev.rawY > loc[1] && ev.rawX < loc[0] + currentFocus.width
                && ev.rawY < loc[1] + currentFocus.height)
    }


    fun validatesFields() : Boolean{

        if(txt_first_name.text.toString().trim() == ""){
            txt_first_name.error = "First Name Required"
            txt_first_name.requestFocus()
            return false
        }

        if(txt_last_name.text.toString().trim() == ""){
            txt_last_name.error = "Last Name Required"
            txt_last_name.requestFocus()
            return false
        }

        if(txt_email.text.toString().trim() == ""){
            txt_email.error = "Email Required"
            txt_email.requestFocus()
            return false
        }
        val password = txt_password.text.toString().trim()
        val confirm_password = txt_confirm_password.text.toString().trim()

        if(password == "" || password.length < 10){
            txt_password.error = "Length should be medium or strong"
            txt_password.requestFocus()
            return false
        }

        if(password != confirm_password){
            txt_confirm_password.error = "Should match with password"
            txt_confirm_password.requestFocus()
            return false
        }
        val contact_number = txt_contact_number.text.toString().trim()

        if(contact_number == "" || contact_number.length < 11){
            txt_contact_number.error = "Valid contact number is required"
            txt_contact_number.requestFocus()
            return false
        }

        if(txt_location.text.toString() == ""){
            txt_location.error = "Valid contact number is required"
            txt_location.requestFocus()
            return false
        }

        if(service_dropdown.visibility == View.VISIBLE && service_dropdown.selectedItem.toString() == "Select"){
            var errorMessage = service_dropdown.selectedView as TextView
            errorMessage.error = "Please select value"
            return false
        }
        return true
    }
}
