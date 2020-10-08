package com.example.helpfinders.utils

class AppPreferencesHandler {

    companion object {

        fun setUID(uid : String?){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()

            app_prefer_editor.putString("uid", uid)
            app_prefer_editor.commit()
        }

        fun getUID() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "uid", null
            )
        }

        fun setIsCustomer(isCustomer : Boolean){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putBoolean("isCustomer", isCustomer)
            app_prefer_editor.commit()
        }

        fun getIsCustomer() : Boolean{
            return AppPreferencesManager.getApplicationPreferences().getBoolean(
                "isCustomer", false
            )
        }


        fun setIsProvider(isProvider : Boolean){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putBoolean("isProvider", isProvider)
            app_prefer_editor.commit()
        }

        fun getIsProvider() : Boolean{
            return AppPreferencesManager.getApplicationPreferences().getBoolean(
                "isProvider", false
            )
        }


        fun setUserLocation(userLocation : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()

            app_prefer_editor.putString("userLocation", userLocation)
            app_prefer_editor.commit()
        }

        fun getUserLocation() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "userLocation", ""
            )
        }

        fun setFirstName(firstName : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("firstName", firstName)
            app_prefer_editor.commit()
        }

        fun getFirstName() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "firstName", ""
            )
        }

        fun setLastName(lastName : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("lastName", lastName)
            app_prefer_editor.commit()
        }

        fun getLastName() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "lastName", ""
            )
        }


        fun setUserEmail(userEmail : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("userEmail", userEmail)
            app_prefer_editor.commit()
        }

        fun getUserEmail() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "userEmail", ""
            )
        }

        fun setMobileNumber(mobileNumber : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("mobileNumber", mobileNumber)
            app_prefer_editor.commit()
        }

        fun getMobileNumber() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "mobileNumber", ""
            )
        }

        fun setProfileImage(profileImage : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("profileImage", profileImage)
            app_prefer_editor.commit()
        }

        fun getProfileImage() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "profileImage", ""
            )
        }

        fun setServiceType(serviceType : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("serviceType", serviceType)
            app_prefer_editor.commit()
        }

        fun getServiceType() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "serviceType", ""
            )
        }
        fun setPassword(password : String){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putString("password", password)
            app_prefer_editor.commit()
        }

        fun getPassword() : String?{
            return AppPreferencesManager.getApplicationPreferences().getString(
                "password", ""
            )
        }
        fun setUserStatus(userstatus : Boolean){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putBoolean("userstatus", userstatus)
            app_prefer_editor.commit()
        }

        fun getUserStatus() : Boolean?{
            return AppPreferencesManager.getApplicationPreferences().getBoolean(
                "userstatus", false
            )
        }

        fun setRememberMe(rememberme : Boolean){
            val app_prefer_editor = AppPreferencesManager.getApplicationPreferencesEditor()
            app_prefer_editor.putBoolean("rememberme", rememberme)
            app_prefer_editor.commit()
        }

        fun getRememberMe() : Boolean?{
            return AppPreferencesManager.getApplicationPreferences().getBoolean(
                "rememberme", false
            )
        }



        fun clearStoredData(){

            setUID(null)
            setUserStatus(false)
            setUserLocation("")
            setUserEmail("")
            setMobileNumber("")
            setProfileImage("")
            setIsProvider(false)
            setIsCustomer(false)
            setServiceType("")
            setUserStatus(false)
            setFirstName("")
            setLastName("")
            setRememberMe(false)

        }

    }


}