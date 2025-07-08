package com.zebra.nilac.igplayground.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.emdkloader.utils.SignatureUtils
import com.zebra.nilac.igplayground.ui.BaseViewModel

class UserAuthenticationViewModel(private var application: Application) :
    BaseViewModel(application) {

    val userAuthenticationPermissions: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun acquirePermissionsForUserAuthentication() {
        acquirePermissionForAuthRequest()
    }

    private fun acquirePermissionForAuthRequest() {
        Log.i(TAG, "Acquiring permission to allow authentication requests")
        processIGAPIAuthorization {
            val profile = """
                <wap-provisioningdoc>
                    <characteristic type="Profile">
                        <parm name="ProfileName" value="IGStartAuth" />
                        <parm name="ModifiedDate" value="2024-07-10 18:36:07" />
                        <parm name="TargetSystemVersion" value="10.4" />
                
                        <characteristic type="AccessMgr" version="10.4">
                            <parm name="emdk_name" value="" />
                            <parm name="ServiceAccessAction" value="4" />
                            <parm name="ServiceIdentifier" value="content://com.zebra.mdna.els.provider/lockscreenaction/startauthentication" />
                            <parm name="CallerPackageName" value="${application.packageName}" />
                            <parm name="CallerSignature"
                                value="${SignatureUtils.getAppSigningCertificate(application)}" />
                        </characteristic>
                    </characteristic>
                </wap-provisioningdoc>"""

            ProfileLoader().processProfile(
                "IGStartAuth",
                profile,
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
        processIGAPIAuthorization {
            val profile = """
                <wap-provisioningdoc>
                    <characteristic type="Profile">
                        <parm name="ProfileName" value="IGAuthStatus" />
                        <parm name="ModifiedDate" value="2024-07-10 18:36:07" />
                        <parm name="TargetSystemVersion" value="10.4" />
                
                        <characteristic type="AccessMgr" version="10.4">
                            <parm name="emdk_name" value="" />
                            <parm name="ServiceAccessAction" value="4" />
                            <parm name="ServiceIdentifier" value="content://com.zebra.mdna.els.provider/lockscreenaction/authenticationstatus" />
                            <parm name="CallerPackageName" value="${application.packageName}" />
                            <parm name="CallerSignature"
                                value="${SignatureUtils.getAppSigningCertificate(application)}" />
                        </characteristic>
                    </characteristic>
                </wap-provisioningdoc>"""

            ProfileLoader().processProfile(
                "IGAuthStatus",
                profile,
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
    }

    companion object {
        const val TAG = "UserAuthenticationViewModel"
    }
}