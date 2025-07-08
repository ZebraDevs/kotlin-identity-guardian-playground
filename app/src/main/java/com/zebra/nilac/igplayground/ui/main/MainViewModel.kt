package com.zebra.nilac.igplayground.ui.main

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.AppConstants
import com.zebra.nilac.igplayground.StatusService
import com.zebra.nilac.igplayground.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private var application: Application) : BaseViewModel(application) {

    val userSessionData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val previousUserSessionData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val logoutEvent: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val lockscreenStatusStatePermission: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun getCurrentUserSession(permissionGrantStatus: Boolean = true) {
        var userSession = ""

        if (!permissionGrantStatus) {
            Log.e(TAG, "Failed to acquire permission")
            userSessionData.postValue("")
        }

        viewModelScope.launch(Dispatchers.IO) {
            application.contentResolver.query(
                Uri.parse(AppConstants.CURRENT_SESSION_URI),
                null,
                null,
                null
            ).use {
                if (it == null || it.columnCount == 0) {
                    Log.e(TAG, "App is not having permission for this API")
                    acquirePermissionForCurrentUserSession()
                    return@launch
                }

                Log.i(TAG, "Acquiring user session data")
                while (it.moveToNext()) {
                    for (i in 0 until it.columnCount) {
                        userSession = "$userSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
                    }
                }

                userSessionData.postValue(userSession)
            }
        }
    }

    fun getPreviousUserSession(permissionGrantStatus: Boolean = true) {
        var previousUserSession = ""

        if (!permissionGrantStatus) {
            Log.e(TAG, "Failed to acquire permission")
            previousUserSessionData.postValue("")
        }

        viewModelScope.launch(Dispatchers.IO) {
            application.contentResolver.query(
                Uri.parse(AppConstants.PREVIOUS_SESSION_URI),
                null,
                null,
                null
            ).use {
                if (it == null || it.columnCount == 0) {
                    Log.e(TAG, "App is not having permission for this API")
                    acquirePermissionForPreviousUserSession()
                    return@launch
                }

                Log.i(TAG, "Acquiring previous user session data")
                while (it.moveToNext()) {
                    for (i in 0 until it.columnCount) {
                        previousUserSession =
                            "$previousUserSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
                    }
                }

                previousUserSessionData.postValue(previousUserSession)
            }
        }
    }

    fun performIGLogout() {
        Log.i(TAG, "Acquiring permission to logout current user")
        processIGAPIAuthorization {
            ProfileLoader().processProfile(
                "IGLogout",
                null,
                object : ProfileLoaderResultCallback {
                    override fun onProfileLoadFailed(errorObject: EMDKResults) {
                        //Nothing to see here..
                    }

                    override fun onProfileLoadFailed(message: String) {
                        Log.e(TAG, "Failed to process profile")
                        logoutEvent.postValue(false)
                    }

                    override fun onProfileLoaded() {
                        val response = application.contentResolver.call(
                            Uri.parse(AppConstants.BASE_URI),
                            AppConstants.LOCKSCREEN_ACTION,
                            AppConstants.LOGOUT_METHOD,
                            null
                        );

                        Log.w(TAG, "LOCK STATE: ${response?.getString("RESULT")}")
                    }
                })
        }
    }

    fun acquirePermissionForLockscreenStatusState() {
        Log.i(TAG, "Acquiring permission for lockscreen status state")
        processIGAPIAuthorization {
            ProfileLoader().processProfile(
                "IGLockscreenStatus",
                null,
                object : ProfileLoaderResultCallback {
                    override fun onProfileLoadFailed(errorObject: EMDKResults) {
                        //Nothing to see here..
                    }

                    override fun onProfileLoadFailed(message: String) {
                        Log.e(TAG, "Failed to process profile")
                        lockscreenStatusStatePermission.postValue(false)
                    }

                    override fun onProfileLoaded() {
                        application.startForegroundService(
                            Intent(
                                application,
                                StatusService::class.java
                            )
                        )
                    }
                })
        }
    }

    private fun acquirePermissionForCurrentUserSession() {
        Log.i(TAG, "Acquiring permission to check the current user session")
        processIGAPIAuthorization {
            ProfileLoader().processProfile(
                "IGCurrentSession",
                null,
                object : ProfileLoaderResultCallback {
                    override fun onProfileLoadFailed(errorObject: EMDKResults) {
                        //Nothing to see here..
                    }

                    override fun onProfileLoadFailed(message: String) {
                        Log.e(TAG, "Failed to process profile")
                        getCurrentUserSession(false)
                    }

                    override fun onProfileLoaded() {
                        getCurrentUserSession()
                    }
                })
        }
    }

    private fun acquirePermissionForPreviousUserSession() {
        Log.i(TAG, "Acquiring permission to check the previous user session")
        ProfileLoader().processProfile(
            "IGPreviousSession",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                    getPreviousUserSession(false)
                }

                override fun onProfileLoaded() {
                    getPreviousUserSession()
                }
            })
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}