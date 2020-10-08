package com.example.helpfinders.provider.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.letsride.R
import com.example.helpfinders.activities.MainActivity
import com.example.helpfinders.activities.ProviderMainActivity
import com.example.helpfinders.model.Review
import com.example.helpfinders.model.User
import com.example.helpfinders.model.googledirectionapi.Route
import com.example.helpfinders.state.DirectionApiStateEvent
import com.example.helpfinders.provider.ui.chat.ChatFragment
import com.example.helpfinders.utils.Constants
import com.example.helpfinders.utils.DataState
import com.example.helpfinders.utils.DataStateListener
import com.example.helpfinders.utils.Utils
import com.example.helpfinders.viewmodel.DirectionApiViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.PolyUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), OnMapReadyCallback,
    DataStateListener {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var servicesOptions : MutableList<String>
    lateinit var mMap : GoogleMap
    var latitude : Double? = null
    var longitude : Double? = null
    lateinit var userMarker : Marker
    lateinit var placesClient: PlacesClient
    lateinit var mMapFragment: SupportMapFragment
    lateinit var mAutoCompleteFragment : AutocompleteSupportFragment
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var directionApiViewModel: DirectionApiViewModel
    lateinit var dataStateHandler : DataStateListener
    lateinit var searchedProviderList : MutableList<User>
    lateinit var otherProviderList : MutableList<User>
    lateinit var customerUsersList : MutableList<User>
    lateinit var selectedProvider : User
    lateinit var userName : TextView
    lateinit var userProfileImage : ImageView
    lateinit var ratingBar: RatingBar

    companion object{
        var currentUser : User? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
        firebaseAuth = FirebaseAuth.getInstance()
        searchedProviderList = ArrayList()
        otherProviderList = ArrayList()
        customerUsersList = ArrayList()

        val headerView = ProviderMainActivity.navigation.getHeaderView(0)

        userProfileImage = headerView.findViewById(R.id.user_profileimage)
        userName = headerView.findViewById(R.id.tvusername)
        ratingBar = headerView.findViewById(R.id.rating)

        ChatFragment.listenForLatestMessages(activity)
        initResources()
        fetchCurrentUser()
        registerListeners()
        subscribeObservers()
        initPlaces()

    }

    fun initResources(){

        directionApiViewModel = this.context.run {
            this.let {
                ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application).create(
                    DirectionApiViewModel::class.java)
            }
        } ?:throw Exception("Invalid Activity")


        try{
            dataStateHandler = this as DataStateListener
        }catch(e: ClassCastException){
            println("$context must implement DataStateListener")
        }

    }
    //fetching currently logged in user data
    private fun fetchCurrentUser(){

        val uid = firebaseAuth.uid
        databaseReference = firebaseDatabase.getReference("/users/$uid")
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(
                    User::class.java)
                setUpUserData(currentUser!!)
               // if(currentUser!!.isProvider){
                    //hide search incase of provider login and show online offline option to provider
                    rel_provider_status.visibility = View.VISIBLE
                    ly_provider_search.visibility = View.GONE

//                }else{
//                    getLastLocation()
//                    setDropDownHeight()
//                    eventListener()
//                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }




    //setup user info in navigation drawer header of menu
    fun setUpUserData(loggedUser : User){

        val loggedInUser = loggedUser

        if (loggedInUser.profileImage == "") {
            userProfileImage.setImageResource(R.drawable.profile)
        }else{
            Picasso.get().load(loggedInUser.profileImage).into(userProfileImage)
        }
        userName.text = "${loggedInUser.firstName} ${loggedInUser.lastName}"

        var firebaseRef = FirebaseDatabase.getInstance().getReference("users-reviews/${loggedInUser.uid}")

        var rating = 0.0
        var count = 0
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    count++
                    val review = it.getValue(Review::class.java)
                    rating += review?.rating!!
                }

                rating = (rating/count).toDouble()

                ratingBar.rating = rating.toFloat()
            }

        })



    }


    private fun initPlaces(){
        mMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mMapFragment.getMapAsync(this)

        if(!Places.isInitialized()){
            Places.initialize(activity!!, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(activity!!)
    }

    fun registerListeners(){
        status_switch_btn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){

                val uid = firebaseAuth.uid

                val firebaseUserRef = firebaseDatabase.getReference("/users/$uid/userstatus")
                firebaseUserRef.setValue(isChecked)
                    .addOnSuccessListener {
                        Toast.makeText(activity!!, "You are Online Now!!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity!!, "Failed to update User Status", Toast.LENGTH_SHORT).show()
                    }
            }else{
                val uid = firebaseAuth.uid

                val firebaseUserRef = firebaseDatabase.getReference("/users/$uid/userstatus")
                firebaseUserRef.setValue(isChecked)
                    .addOnSuccessListener {
                        Toast.makeText(activity!!, "You are Offline Now!!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity!!, "Failed to update User Status", Toast.LENGTH_SHORT).show()
                    }
            }
        }


    }

    private fun subscribeObservers(){

        //setting up datastate it will send api call request
        directionApiViewModel._direction_api_data_state.observe(activity!!, Observer { dataState ->

            println("DEBUG DataState : $dataState")
            //handling loading
            dataStateHandler.onDataStateChange(dataState)

            dataState.data?.let {directionApiResponse ->
                directionApiViewModel.setDirectionApiResponse(directionApiResponse)
            } ?: showError(dataState.message)


        })

        //updating the views state when data returned
        directionApiViewModel.direction_api_view_state.observe(activity!!, Observer {viewState ->

            println("Status $viewState")

            viewState?.let {
                println("DEBUG Setting Data ... $viewState")

                if(it.status.equals("OK")){
                    val legs = it.routes[0].legs[0]
                    val route =
                        Route(
                            legs.start_address!!,
                            legs.end_address!!,
                            legs.start_location?.lat,
                            legs.start_location?.lng,
                            legs.end_location?.lat,
                            legs.end_location?.lng,
                            it.routes[0].overview_polyline?.points!!
                        )
                    setMarkersAndRoute(route) //calling route function
                    //showing provider information
                    rel_provider_info.visibility = View.VISIBLE
                    tv_arrival_time.text = legs.duration?.text
                    tv_username.text = "${selectedProvider.firstName} ${selectedProvider.lastName}"

                    if (selectedProvider.profileImage != ""){
                        Picasso.get().load(selectedProvider.profileImage).into(img_user_profile)
                    }

                    chat_btn.setOnClickListener { view->
                        val bundle = bundleOf("user" to selectedProvider)
                        Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_chatLogFragment, bundle)
                    }

                }

            }

        })
    }


    private var mRouteMarkerList = ArrayList<Marker>()
    private lateinit var mRoutePolyline: Polyline
    fun setMarkersAndRoute(route: Route) { //this function is used to draw route between start and end point
        val startLatLng = LatLng(route.startLat!!, route.startLng!!)
        val startMarkerOptions: MarkerOptions = MarkerOptions().position(startLatLng).title(route.startName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        val endLatLng = LatLng(route.endLat!!, route.endLng!!)
        val endMarkerOptions: MarkerOptions = MarkerOptions().position(endLatLng).title(route.endName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        val startMarker = mMap.addMarker(startMarkerOptions)
        val endMarker = mMap.addMarker(endMarkerOptions)
        mRouteMarkerList.add(startMarker)
        mRouteMarkerList.add(endMarker)

        val polylineOptions = drawRoute(activity!!)
        val pointsList = PolyUtil.decode(route.overviewPolyline)
        for (point in pointsList) {
            polylineOptions.add(point)
        }

        mRoutePolyline = mMap.addPolyline(polylineOptions)

       // mMap.animateCamera(autoZoomLevel(mRouteMarkerList))
    }

    fun clearMarkersAndRoute() { //this function is used to clear markers and routes from map
        for (marker in mRouteMarkerList) {
            marker.remove()
        }
        mRouteMarkerList.clear()

        if (::mRoutePolyline.isInitialized) {
            mRoutePolyline.remove()
        }
    }


    //used to draw marker on start and end points but not using right now
    fun drawMarker(context: Context, text: String): Bitmap {
        val drawable = context.resources.getDrawable(R.drawable.ic_black_marker)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        val paint = Paint()
        paint.textSize = 50 * context.resources.displayMetrics.density / 2
        paint.style = Paint.Style.FILL
        val textCanvas = Canvas(bitmap)
        textCanvas.drawText(text, ((bitmap.width * 7) / 20).toFloat(), (bitmap.height / 2).toFloat(), paint)

        return bitmap
    }

    //function is used to draw route between start and end location
    fun drawRoute(context: Context): PolylineOptions {
        val polylineOptions = PolylineOptions()
        polylineOptions.width(10.toFloat())
        polylineOptions.geodesic(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            polylineOptions.color(context.resources.getColor(R.color.black, context.theme))
        } else {
            polylineOptions.color(context.resources.getColor(R.color.black))
        }
        return polylineOptions
    }

    private fun showError(message : String?){
        Toast.makeText(activity!!, "$message", Toast.LENGTH_SHORT).show()
    }

    override fun onDataStateChange(dataState: DataState<*>?) {

        if (dataState!!.loading){
            progress_bar.visibility = View.VISIBLE
        }else{
            progress_bar.visibility = View.GONE
        }
    }

    fun triggerDirectionApi(destination : String){
        directionApiViewModel.setDirectionApiResponseStateEvent(DirectionApiStateEvent.GetDirectionApi(getAddressFromLocation(), destination, getString(R.string.google_maps_key)))
    }

    //this is used to create dropdown of provider services
    fun setServicesAdapter(searchedService : String){
        servicesOptions = ArrayList()
        var uid = firebaseAuth.uid
        databaseReference= firebaseDatabase.getReference("/services")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    progress_bar.visibility = View.GONE
                    p0.children.forEach {
                        servicesOptions.add(it.value.toString())
                        setDropDownServices()
                    }
                }
            })

    }

    private fun setDropDownServices(){
        var arrayAdapter = object : ArrayAdapter<String>(activity!!, R.layout.layout_custom_dropdown, servicesOptions) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }

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

        searchView.setAdapter(arrayAdapter)
    }


    fun getUserAddresses(user : User, serviceName : String, providerCount : Int){

        if(user.isProvider && user.serviceType ==  serviceName){
            val address = user.userLocation
//            triggerDirectionApi(address)
            getLocationFromAddress(address, user, providerCount)
        }else{
            Toast.makeText(activity, "Provider Not Available for this service", Toast.LENGTH_SHORT).show()
        }

    }

    fun getLocationFromAddress(address : String, user : User, providerCount: Int){

        var geoCoder = Geocoder(activity)
        lateinit var addresses : MutableList<Address>

        try{
            addresses = geoCoder.getFromLocationName(address, 5)
            addresses?.let {
                for (location in addresses) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getMinimumDistance(latitude, longitude, address, user, providerCount)
                    val latlng = LatLng(latitude, longitude)
//                    mMap.setOnInfoWindowClickListener(this)
//                    userMarker = mMap.addMarker(MarkerOptions()
//                        .position(latlng)
//                        .title("${user.firstName} ${user.lastName} (${user.serviceType})")
//                        .snippet("Provider")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
//                    userMarker.tag = user
                }
            }
        }catch (e : java.lang.Exception){
            Toast.makeText(activity, "$e", Toast.LENGTH_SHORT).show()
        }

    }

    //getting provider with minimum distance to customer and choosing when provider is at closest distance
    var lastDistance : Double? =  null
    var newDistance : Double? =  null
    fun getMinimumDistance(lat : Double, lng : Double, address : String, user : User, providerCount: Int){

        val startPoint = Location("locationA")
        startPoint.latitude = latitude!!
        startPoint.longitude = longitude!!

        val endPoint = Location("LocationA")
        endPoint.latitude = lat
        endPoint.longitude = lng
        if (lastDistance == null){
            lastDistance = startPoint.distanceTo(endPoint).toDouble()
        }else{
            newDistance = startPoint.distanceTo(endPoint).toDouble()
            if(newDistance!! < lastDistance!!){
                lastDistance = newDistance
            }
        }

        if (providerCount == searchedProviderList.size){
            selectedProvider = user
            triggerDirectionApi(address)
        }


    }

    //this function is used to get address from lat and lng of the logged in user
    fun getAddressFromLocation() : String{

        val addresses: List<Address>
        val geocoder: Geocoder = Geocoder(activity, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude!!,
            longitude!!,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

        return "$address,$city,$country"
    }


//    override fun onInfoWindowClick(marker: Marker?) {
//
//        var user = marker?.tag as User
//        var bundle = bundleOf("user" to user)
//        Navigation.findNavController(view!!).navigate(R.id.action_nav_home_to_nav_chatLogFragment, bundle)
//
//
//    }
    //getting user last location
    private fun getLastLocation(){

        if(checkPermissions()){
            if(isLocationEnabled()){
                mFusedLocationClient.lastLocation
                    .addOnCompleteListener {task ->  
                        var location : Location? = task.result
                        location?.let {
                            latitude = it.latitude
                            longitude = it.longitude
                            val latlng = LatLng(latitude!!, longitude!!)
                            mMap.addMarker(MarkerOptions().position(latlng).title("Pakistan"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f))

                            println("Current Location : ${it.latitude.toString()}, ${it.longitude.toString()}")
                        } ?: requestNewLocationData()
                    }
            }else{
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(settingsIntent)
            }
        }else{
            requestPermissions()
        }


    }

    //request new location if user last location data is null or somehow not available
    private fun requestNewLocationData(){
        var locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //locationRequest.numUpdates = 1 //it is commented to get user location in realtime
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
    }

    //locationCallBack is called when an update is received

    var locationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult : LocationResult?) {
            var lastLocation = locationResult!!.lastLocation
            latitude = lastLocation.latitude
            longitude = lastLocation.longitude

            val latlng = LatLng(latitude!!, longitude!!)
            mMap.addMarker(MarkerOptions().position(latlng).title("Pakistan"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17f))
            println("Current Location : ${lastLocation?.latitude.toString()}, ${lastLocation?.longitude.toString()}")

        }
    }

    //checking if location is turned on in settings
    private fun isLocationEnabled() : Boolean{
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    //checking if permisions are given
    private fun checkPermissions() : Boolean{
        if(ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    //requesting location permissions
    private fun requestPermissions(){
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), Constants.LOCATION_PERMISSION)
    }

    //handling after the permission are allowed
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.LOCATION_PERMISSION){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //to do after the location permission are given
                getLastLocation()

            }else{
                Toast.makeText(activity, "Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        getLastLocation()
    }


}
