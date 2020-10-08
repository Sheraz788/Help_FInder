package com.example.helpfinders.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.letsride.R
import com.example.helpfinders.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.zopim.android.sdk.api.ZopimChat
import com.zopim.android.sdk.prechat.EmailTranscript
import com.zopim.android.sdk.prechat.PreChatForm
import com.zopim.android.sdk.widget.ChatWidgetService

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var navController : NavController

    companion object{
        lateinit var navigation : NavigationView
        var config : ZopimChat.SessionConfig? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        navigation = findViewById(R.id.nav_customer_view)

       // showMainActivityContent()
        initZendesk()
        setUpDrawerMenu()
        eventListeners()
    }



    internal fun initZendesk() {
        ZopimChat.init(getString(R.string.zopim_chat_account_key))

        // build pre chat form config
        val preChatForm = PreChatForm.Builder()
            .name(PreChatForm.Field.REQUIRED)
            .email(PreChatForm.Field.REQUIRED)
            .phoneNumber(PreChatForm.Field.OPTIONAL)
            .department(PreChatForm.Field.OPTIONAL)
            .message(PreChatForm.Field.REQUIRED)
            .build()
        // build session config
        config = ZopimChat.SessionConfig()
            .preChatForm(preChatForm)
            .emailTranscript(EmailTranscript.DISABLED)
        ChatWidgetService.disable()
    }

    private fun eventListeners(){

        navController = findNavController(R.id.nav_host_fragment)


//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//
//            if(destination.id == R.id.nav_logout || destination.id == R.id.registerFragment){
//                FirebaseAuth.getInstance().signOut()
//               // hideMainActivityContent()
//                toolbar.visibility = View.GONE
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//
//            }else{
//               // hideMainActivityContent()
//                toolbar.visibility = View.VISIBLE
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//            }
//        }


//        navigation.setNavigationItemSelectedListener { menuItem ->
//
//            var id = menuItem.itemId
//
//            when(id){
//
//                R.id.nav_help -> {
//
//
//                    navController.navigate(R.id.nav_help)
//                    settingZendesk()
//                    true
//                }
//                R.id.nav_logout -> {
//                    var intent = Intent(this, LoginActivity::class.java)
//                    FirebaseAuth.getInstance().signOut()
//                    startActivity(intent)
//                     true
//                }
//                else ->  false
//            }
//
//        }
    }


    private fun setUpDrawerMenu(){
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_customer_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            navController.graph
        , drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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


    private fun isTouchInsideView(ev: MotionEvent, currentFocus: View): Boolean {
        val loc = IntArray(2)
        currentFocus.getLocationOnScreen(loc)
        return (ev.rawX > loc[0] && ev.rawY > loc[1] && ev.rawX < loc[0] + currentFocus.width
                && ev.rawY < loc[1] + currentFocus.height)
    }


}
