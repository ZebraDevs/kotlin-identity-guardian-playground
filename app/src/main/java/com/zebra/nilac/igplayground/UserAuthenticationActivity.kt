package com.zebra.nilac.igplayground

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.databinding.UserAuthenticationBinding


class UserAuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: UserAuthenticationBinding

    private var selectedScheme = ""
    private var selectedFlag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Authenticate User")

        setSupportActionBar(binding.toolbar)
        fillUi()

        binding.sendAuthRequestBtn.setOnClickListener {
            if (!EMDKLoader.getInstance().isManagerInit()) {
                Toast.makeText(this, "EMDK Manager is not yet initialized!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            sendAuthenticationRequest()
        }
    }

    private fun fillUi() {
        val authenticationSchemesAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.authenticationSchemes)
        )

        binding.authenticationSchemeInput.setAdapter(authenticationSchemesAdapter)

        val flagsAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.flags)
        )

        binding.flagsInput.setAdapter(flagsAdapter)
    }

    private fun sendAuthenticationRequest() {
        val bundle = Bundle().apply {
            putString("user_verification", binding.authenticationSchemeInput.text.toString())
            putString("launchflag", binding.flagsInput.text.toString())
        }

        val response = contentResolver.call(
            Uri.parse(BASE_URI),
            "lockscreenaction",
            "startauthentication",
            bundle
        );

        if (response == null || !response.containsKey("RESULT") || response.getString("RESULT") == "Caller is unauthorized") {
            Log.e(TAG, "App is not having permission for this API")
            acquirePermissionForUserSession()
            return
        }

        Log.i(TAG, "${response.getString("RESULT")}")
        Toast.makeText(this, "Authentication request successfully sent!", Toast.LENGTH_LONG).show()
        finishAffinity()
    }

    private fun acquirePermissionForUserSession() {
        Log.i(TAG, "Acquiring permission to allow authentication requests")
        ProfileLoader().processProfile(
            "IGStartAuth",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                }

                override fun onProfileLoaded() {
                    sendAuthenticationRequest()
                }
            })
    }

    companion object {
        const val TAG = "UserAuthenticateActivity"

        const val START_AUTHENTICATION_URI =
            "content://com.zebra.mdna.els.provider/lockscreenaction/startauthentication"
        const val BASE_URI =
            "content://com.zebra.mdna.els.provider/"
    }
}