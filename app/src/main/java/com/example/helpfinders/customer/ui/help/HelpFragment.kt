package com.example.helpfinders.customer.ui.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.letsride.R
import com.example.helpfinders.activities.MainActivity
import com.example.helpfinders.customer.ui.home.HomeFragment
import com.zopim.android.sdk.api.ZopimChat
import com.zopim.android.sdk.model.VisitorInfo
import com.zopim.android.sdk.prechat.ZopimChatActivity

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingZendesk()
        findNavController().popBackStack()
    }


    public fun settingZendesk(){
        val visitorInfo = VisitorInfo.Builder()
            .email(HomeFragment.currentUser?.userEmail)
            .name(HomeFragment.currentUser?.firstName + " " + HomeFragment.currentUser?.lastName)
            .build()
        ZopimChat.setVisitorInfo(visitorInfo)
        ZopimChatActivity.startActivity(activity!!, MainActivity.config)
    }

}