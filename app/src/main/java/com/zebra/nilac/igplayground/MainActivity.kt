package com.zebra.nilac.igplayground

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.currentSessionBtn.setOnClickListener {
            if (!EMDKLoader.getInstance().isManagerInit()) {
                Toast.makeText(this, "EMDK Manager is not yet initialized!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            getCurrentUserSession()
        }

        binding.previousSessionBtn.setOnClickListener {
            if (!EMDKLoader.getInstance().isManagerInit()) {
                Toast.makeText(this, "EMDK Manager is not yet initialized!", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            getPreviousUserSession()
        }
    }

    override fun onResume() {
        super.onResume()
        initEMDKManager()
    }

    override fun onDestroy() {
        super.onDestroy()
        EMDKLoader.getInstance().release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_authenticate -> {
                startActivity(Intent(this@MainActivity, UserAuthenticationActivity::class.java))
                return true
            }

            R.id.action_logout -> {
                acquirePermissionForLogoutRequest()
                return true
            }

            R.id.action_start_service -> {
                acquirePermissionForLockscreenStatusState()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initEMDKManager() {
        //Initialising EMDK First...
        Log.i(TAG, "Initialising EMDK Manager")

        if (EMDKLoader.getInstance().isManagerInit()) {
            getCurrentUserSession()
            return
        }

        EMDKLoader.getInstance().initEMDKManager(this, object : EMDKManagerInitCallBack {
            override fun onFailed(message: String) {
                Log.e(TAG, "Failed to initialise EMDK Manager")
            }

            override fun onSuccess() {
                Log.i(TAG, "EMDK Manager was successfully initialised")

                //Automatically get the current user (if available)
                getCurrentUserSession()
            }
        })
    }

    private fun getCurrentUserSession() {
        var userSession = ""

        contentResolver.query(
            Uri.parse(AppConstants.CURRENT_SESSION_URI),
            null,
            null,
            null
        ).use {
            if (it == null || it.columnCount == 0) {
                Log.e(TAG, "App is not having permission for this API")
                acquirePermissionForUserSession()
                return
            }

            while (it.moveToNext()) {
                for (i in 0 until it.columnCount) {
                    userSession = "$userSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
                }
            }
        }
        runOnUiThread {
            binding.userSession.text = userSession
        }
    }

    private fun getPreviousUserSession() {
        var userSession = ""

        contentResolver.query(
            Uri.parse(AppConstants.PREVIOUS_SESSION_URI),
            null,
            null,
            null
        ).use {
            if (it == null || it.columnCount == 0) {
                Log.e(TAG, "App is not having permission for this API")
                acquirePermissionForPreviousUserSession()
                return
            }

            while (it.moveToNext()) {
                for (i in 0 until it.columnCount) {
                    userSession = "$userSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
                }
            }
        }
        runOnUiThread {
            binding.userSession.text = userSession
        }
    }

    private fun sendLogoutRequest() {
        val response = contentResolver.call(
            Uri.parse(AppConstants.BASE_URI),
            AppConstants.LOCKSCREEN_ACTION,
            AppConstants.LOGOUT_METHOD,
            null
        );

        Log.w(TAG, "LOCK STATE: ${response?.getString("RESULT")}")
    }

    private fun acquirePermissionForUserSession() {
        Log.i(TAG, "Acquiring permission to check the current user session")
        ProfileLoader().processProfile(
            "IGCurrentSession",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                }

                override fun onProfileLoaded() {
                    getCurrentUserSession()
                }
            })
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
                }

                override fun onProfileLoaded() {
                    getPreviousUserSession()
                }
            })
    }

    private fun acquirePermissionForLogoutRequest() {
        Log.i(TAG, "Acquiring permission to logout a user")
        ProfileLoader().processProfile(
            "IGLogout",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                }

                override fun onProfileLoaded() {
                    sendLogoutRequest()
                }
            })
    }

    private fun acquirePermissionForLockscreenStatusState() {
        Log.i(TAG, "Acquiring permission for lockscreen status state")
        ProfileLoader().processProfile(
            "IGLockscreenStatus",
            null,
            object : ProfileLoaderResultCallback {
                override fun onProfileLoadFailed(errorObject: EMDKResults) {
                    //Nothing to see here..
                }

                override fun onProfileLoadFailed(message: String) {
                    Log.e(TAG, "Failed to process profile")
                }

                override fun onProfileLoaded() {
                    startForegroundService(Intent(this@MainActivity, StatusService::class.java))
                }
            })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}