package com.example.helpfinders.customer.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.letsride.R
import com.example.helpfinders.activities.LoginActivity
import com.example.helpfinders.customer.ui.home.HomeFragment
import com.example.helpfinders.utils.AppPreferencesHandler
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.user_profile_img

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEventListeners()
        setUpUserData()
    }

    fun setUpUserData(){

        val currentUser = HomeFragment.currentUser

        if(currentUser != null) {
            if (currentUser.profileImage == "") {
                user_profile_img.setImageResource(R.drawable.profile)
            }else{
                Picasso.get().load(currentUser.profileImage).into(user_profile_img)
            }
            tv_username.text = "${currentUser.firstName} ${currentUser.lastName}"
            tv_user_contact_number.text = currentUser.mobileNumber
            tv_user_email.text = currentUser.userEmail
        }
    }



    fun setEventListeners(){
        rel_user_profile.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_nav_settings_to_nav_update_profile)
        }


        rel_logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            AppPreferencesHandler.clearStoredData()
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)

        }

    }

}
