package com.example.helpfinders.customer.ui.pastservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.letsride.R

class PastServicesFragment : Fragment() {

    private lateinit var notificationsModel: NotificationsModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsModel =
                ViewModelProviders.of(this).get(NotificationsModel::class.java)
        val root = inflater.inflate(R.layout.fragment_past_services, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        notificationsModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
