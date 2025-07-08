package com.zebra.nilac.igplayground.ui.auth

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.AppConstants
import com.zebra.nilac.igplayground.R
import com.zebra.nilac.igplayground.databinding.UserAuthenticationBinding
import com.zebra.nilac.igplayground.ui.BaseActivity
import com.zebra.nilac.igplayground.ui.main.MainViewModel

class UserAuthenticationActivity : BaseActivity() {

    private lateinit var binding: UserAuthenticationBinding
    private val userAuthenticationViewModel: UserAuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Authenticate User")
        setSupportActionBar(binding.toolbar)

        fillUi()

        userAuthenticationViewModel.userAuthenticationPermissions.observe(
            this,
            userAuthenticationPermissionsObserver
        )

        binding.sendAuthRequestBtn.setOnClickListener {
            showLoadingScreen()
            sendAuthenticationRequest()
        }

        showLoadingScreen()
        userAuthenticationViewModel.acquirePermissionsForUserAuthentication()
    }

    override fun onDestroy() {
        super.onDestroy()
        userAuthenticationViewModel.userAuthenticationPermissions.removeObservers(this)
        contentResolver.unregisterContentObserver(authStatusContentObserver)

        userAuthenticationViewModel.releaseEMDKManager()
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

    private val authStatusContentObserver = object : ContentObserver(Handler(Looper.myLooper()!!)) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            getAuthRequestStatus()
        }
    }

    private fun sendAuthenticationRequest() {
        val bundle = Bundle().apply {
            putString("user_verification", binding.authenticationSchemeInput.text.toString())
            putString("launchflag", binding.flagsInput.text.toString())
        }

        val response = contentResolver.call(
            Uri.parse(AppConstants.BASE_URI),
            AppConstants.LOCKSCREEN_ACTION,
            AppConstants.START_AUTHENTICATION_METHOD,
            bundle
        );

        contentResolver.registerContentObserver(
            Uri.parse(AppConstants.STATUS_AUTHENTICATION_URI),
            false,
            authStatusContentObserver
        )

        if (response == null || !response.containsKey("RESULT") || response.getString("RESULT") == "Caller is unauthorized") {
            Log.e(
                TAG,
                "App is not having permission for this API or something else unknown has happened during the process"
            )
            dismissLoadingScreen()
            return
        } else if (response.containsKey("RESULT") && response.getString("RESULT") == "SUCCESS") {
            dismissLoadingScreen()
            Toast.makeText(
                this,
                "Session already in use, please log out first before creating a new request",
                Toast.LENGTH_LONG
            ).show()
        } else if (response.containsKey("RESULT") && response.getString("RESULT") == "Error:Cannot initiate as lock type is Device lock") {
            dismissLoadingScreen()
            Toast.makeText(
                this,
                "Unable to launch a new authentication request, please log out first",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Log.w(TAG, "${response.getString("RESULT")}")
        }
    }

    private fun getAuthRequestStatus() {
        var response = ""

        contentResolver.query(
            Uri.parse(AppConstants.STATUS_AUTHENTICATION_URI),
            null,
            null,
            null
        ).use {
            if (it == null || it.columnCount == 0) {
                Log.w(TAG, "Detected a new event but not triggered by our app, ignoring...")
                return
            }

            val bundle = it.extras;
            if (bundle != null && bundle.containsKey("RESULT")) {
                response = bundle.getString("RESULT")!!
            }

//            while (it.moveToNext()) {
//                for (i in 0 until it.columnCount) {
//                    response = "$response\n${it.getColumnName(i)}: ${it.getString(i)}\n"
//                }
//            }
        }
        Log.i(TAG, response)

        dismissLoadingScreen()
        binding.authRequestContainer.visibility = View.GONE
        binding.userSession.text = response
    }

    private val userAuthenticationPermissionsObserver = Observer<Boolean> { status ->
        runOnUiThread {
            dismissLoadingScreen()
            if (!status) {
                Toast.makeText(
                    this,
                    "Unable to grant one or both of the required permissions to request an authentication request for a new user, check the logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }
        }
    }

    companion object {
        const val TAG = "UserAuthenticateActivity"
    }
}