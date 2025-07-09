package com.zebra.nilac.igplayground.ui.lockscreen

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.emdkloader.utils.SignatureUtils
import com.zebra.nilac.igplayground.ui.BaseViewModel
import com.zebra.nilac.igplayground.ui.auth.UserAuthenticationViewModel

class LockScreenMessageManagementViewModel(private var application: Application) :
    BaseViewModel(application) {

    val lockscreenMessagePermission: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun acquirePermissionForLockscreenMessageSupport() {
        Log.i(TAG, "Acquiring permission to manage lockscreen messages")
        processIGAPIAuthorization {
            val profile = """
                <wap-provisioningdoc>
                    <characteristic type="Profile">
                        <parm name="ProfileName" value="IGLockscreenMessage" />
                        <parm name="ModifiedDate" value="2024-07-10 18:36:07" />
                        <parm name="TargetSystemVersion" value="10.4" />
                
                        <characteristic type="AccessMgr" version="10.4">
                            <parm name="emdk_name" value="" />
                            <parm name="ServiceAccessAction" value="4" />
                            <parm name="ServiceIdentifier" value="content://com.zebra.mdna.els.provider/lockscreenaction/showmessage" />
                            <parm name="CallerPackageName" value="${application.packageName}" />
                            <parm name="CallerSignature"
                                value="${SignatureUtils.getAppSigningCertificate(application)}" />
                        </characteristic>
                    </characteristic>
                </wap-provisioningdoc>"""

            ProfileLoader().processProfile(
                "IGLockscreenMessage",
                profile,
                object : ProfileLoaderResultCallback {
                    override fun onProfileLoadFailed(errorObject: EMDKResults) {
                        //Nothing to see here..
                    }

                    override fun onProfileLoadFailed(message: String) {
                        Log.e(TAG, "Failed to process profile")
                        lockscreenMessagePermission.postValue(false)
                    }

                    override fun onProfileLoaded() {
                        lockscreenMessagePermission.postValue(true)
                    }
                })
        }
    }

    companion object {
        const val TAG = "LockScreenMessageManagementViewModel"
    }
}