package com.zebra.nilac.igplayground.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.ui.BaseViewModel

class UserAuthenticationViewModel(application: Application) : BaseViewModel(application) {

    val userAuthenticationPermissions: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun acquirePermissionsForUserAuthentication() {
        acquirePermissionForAuthRequest()
    }

    private fun acquirePermissionForAuthRequest() {
        Log.i(TAG, "Acquiring permission to allow authentication requests")
        processIGAPIAuthorization {
            ProfileLoader().processProfile(
                "IGStartAuth",
                null,
                object : ProfileLoaderResultCallback {
                    override fun onProfileLoadFailed(errorObject: EMDKResults) {
                        //Nothing to see here..
                    }

                    override fun onProfileLoadFailed(message: String) {
                        Log.e(TAG, "Failed to process profile")
                        userAuthenticationPermissions.postValue(false)
                    }

                    override fun onProfileLoaded() {
                        acquirePermissionForAuthStatus()
                    }
                })
        }
    }

    private fun acquirePermissionForAuthStatus() {
        Log.i(TAG, "Acquiring permission to listen for authentication request status")
        ProfileLoader().processProfile(
            "IGAuthStatus",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                    userAuthenticationPermissions.postValue(false)
                }

                override fun onProfileLoaded() {
                    userAuthenticationPermissions.postValue(true)
                }
            })
    }

    companion object {
        const val TAG = "UserAuthenticationViewModel"
    }
}