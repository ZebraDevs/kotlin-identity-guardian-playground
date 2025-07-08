package com.zebra.nilac.igplayground.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.igplayground.AppConstants
import com.zebra.nilac.igplayground.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel(private var application: Application) : AndroidViewModel(application) {

    fun processIGAPIAuthorization(processProfileTask: () -> Unit) {
        val mEmdkLoaderInstance = EMDKLoader.getInstance()

        if (mEmdkLoaderInstance.isManagerInit()) {
            processProfileTask()
            return
        }

        Log.w(TAG, "Initializing EMDK Manager for the first time...")
        mEmdkLoaderInstance.initEMDKManager(
            application.applicationContext,
            object : EMDKManagerInitCallBack {
                override fun onFailed(message: String) {
                    Log.e(TAG, "Failed to initialise EMDK Manager")
                }

                override fun onSuccess() {
                    Log.i(TAG, "EMDK Manager was successfully initialised")
                    processProfileTask()
                }
            })
    }

    fun releaseEMDKManager() {
        Log.i(TAG, "Releasing EMDK Manager")
        EMDKLoader.getInstance().release()
    }

    companion object {
        const val TAG = "BaseViewModel"
    }
}