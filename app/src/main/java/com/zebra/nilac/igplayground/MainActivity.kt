package com.zebra.nilac.igplayground

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.symbol.emdk.EMDKResults
import com.zebra.nilac.emdkloader.EMDKLoader
import com.zebra.nilac.emdkloader.ProfileLoader
import com.zebra.nilac.emdkloader.interfaces.EMDKManagerInitCallBack
import com.zebra.nilac.emdkloader.interfaces.ProfileLoaderResultCallback
import com.zebra.nilac.igplayground.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

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

        initEMDKManager()
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

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initEMDKManager() {
        //Initialising EMDK First...
        Log.i(TAG, "Initialising EMDK Manager")

        EMDKLoader.getInstance().initEMDKManager(this, object : EMDKManagerInitCallBack {
            override fun onFailed(message: String) {
                Log.e(TAG, "Failed to initialise EMDK Manager")
            }

            override fun onSuccess() {
                Log.i(TAG, "EMDK Manager was successfully initialised")
            }
        })
    }

    private fun getCurrentUserSession() {
        var userSession = ""

        contentResolver.query(
            Uri.parse(CURRENT_SESSION_URI),
            null,
            null,
            null
        ).use {
            if (it == null || it.columnCount == 0) {
                Log.e(TAG, "App is not having permission for this API")
                acquirePermissionForUserSession()
                return
            }

            it.moveToNext()
            for (i in 0 until it.columnCount) {
                userSession = "$userSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
            }
        }
        runOnUiThread {
            binding.userSession.text = userSession
        }
    }

    private fun getPreviousUserSession() {
        var userSession = ""

        contentResolver.query(
            Uri.parse(PREVIOUS_SESSION_URI),
            null,
            null,
            null
        ).use {
            if (it == null || it.columnCount == 0) {
                Log.e(TAG, "App is not having permission for this API")
                acquirePermissionForPreviousUserSession()
                return
            }

            it.moveToNext()
            for (i in 0 until it.columnCount) {
                userSession = "$userSession\n${it.getColumnName(i)}: ${it.getString(i)}\n"
            }
        }
        runOnUiThread {
            binding.userSession.text = userSession
        }
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
                    getCurrentUserSession()
                }
            })
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val CURRENT_SESSION_URI =
            "content://com.zebra.mdna.els.provider/currentsession"
        private const val PREVIOUS_SESSION_URI =
            "content://com.zebra.mdna.els.provider/previoussession"
    }
}