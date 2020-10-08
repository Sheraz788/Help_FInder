package com.example.helpfinders.customer.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.letsride.R
import com.example.helpfinders.model.User
import com.example.helpfinders.customer.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_update_profile.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : Fragment() {
    var selectedPhotoUri : Uri? = null
    var profileImageUrl: String = ""
    lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var firebaseStorage : FirebaseStorage
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        setProfileData()
        setEventListeners()
    }

    private fun setProfileData(){
        var currentUser = HomeFragment.currentUser


        //set currently logged in user profile image
        if(currentUser != null) {
            if (currentUser.isCustomer){
                rel_per_hour_charges.visibility = View.GONE
            }
            if (currentUser.profileImage == "") {
                user_profile_img.setImageResource(R.drawable.profile)
            }else{
                Picasso.get().load(currentUser.profileImage).into(user_profile_img)
            }

            txt_first_name.setText(currentUser.firstName)
            txt_last_name.setText(currentUser.lastName)
            txt_contact_number.setText(currentUser.mobileNumber)
            txt_email.setText(currentUser.userEmail)

        }


    }
    private fun setEventListeners(){

        add_image_btn.setOnClickListener {
            chooseProfileImageFromGallery()
        }

        update_btn.setOnClickListener {
            updateProfileData()
        }

    }


    private fun chooseProfileImageFromGallery(){

        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }else {
            var imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val imageIntent = Intent(Intent.ACTION_PICK)
            startActivityForResult(imageIntent, 0)
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //retrieving image data
            selectedPhotoUri = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedPhotoUri)

            user_profile_img.setImageBitmap(bitmap)
            //ly_add_profile_Image.alpha = 0f

            uploadImageToFirebaseStorage()
        }

    }

    fun uploadImageToFirebaseStorage(){

        if(selectedPhotoUri == null){
            Toast.makeText(activity, "Photo must be selected", Toast.LENGTH_SHORT).show()
        }else{

            val fileName = UUID.randomUUID().toString()

            val storageRef = firebaseStorage.getReference("images/$fileName")

            storageRef.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl
                        .addOnSuccessListener {
                            profileImageUrl = it.toString()
                        }

                }

                .addOnFailureListener {
                    Log.d("Upload", "Failed to upload image to storage: ${it.message}")
                    Toast.makeText(activity, "Failed to upload Image", Toast.LENGTH_SHORT).show()
                }
        }
    }


    fun updateProfileData(){
        val uid = FirebaseAuth.getInstance().uid
        var currentUser = HomeFragment.currentUser!!
        val firstName = txt_first_name.text.toString()
        val lastName = txt_last_name.text.toString()
        val mobileNumber = txt_contact_number.text.toString()
        val userEmail = txt_email.text.toString()

        if(profileImageUrl == ""){
            profileImageUrl = currentUser.profileImage
        }

        val user = User(
            currentUser.uid,
            firstName,
            lastName,
            userEmail,
            currentUser.password,
            mobileNumber,
            profileImageUrl,
            currentUser.userLocation,
            currentUser.isProvider,
            currentUser.isCustomer,
            currentUser.userstatus,
            currentUser.rememberme,
            currentUser.serviceType,
            null
        )

        val firebaseDatabaseRef = firebaseDatabase.getReference("users/$uid").setValue(user)
        firebaseDatabaseRef.addOnSuccessListener {
                HomeFragment.currentUser = user
                findNavController().navigate(R.id.action_nav_update_profile_to_nav_settings)
                Toast.makeText(activity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d("Upload", "Failed to upload image to storage: ${it.message}")
                Toast.makeText(activity, "Failed to Save Image", Toast.LENGTH_SHORT).show()
            }
    }

}
