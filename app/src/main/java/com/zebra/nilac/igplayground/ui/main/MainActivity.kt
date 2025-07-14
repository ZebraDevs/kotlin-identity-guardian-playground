package com.zebra.nilac.igplayground.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.color.MaterialColors
import com.zebra.nilac.igplayground.R
import com.zebra.nilac.igplayground.databinding.ActivityMainBinding
import com.zebra.nilac.igplayground.ui.BaseActivity
import com.zebra.nilac.igplayground.ui.auth.UserAuthenticationActivity
import com.zebra.nilac.igplayground.ui.lockscreen.LockScreenMessageManagementActivity
import com.zebra.nilac.igplayground.ui.ms.MSSignInActivity


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.overflowIcon?.setTint(MaterialColors.getColor(binding.toolbar, com.google.android.material.R.attr.colorSecondaryContainer))

        mainViewModel.userSessionData.observe(this, userSessionObserver)
        mainViewModel.previousUserSessionData.observe(this, previousUserSessionObserver)
        mainViewModel.logoutEvent.observe(this, logoutEventObserver)
        mainViewModel.lockscreenStatusStatePermission.observe(this, lockscreenStatusStateObserver)

        binding.currentSessionBtn.setOnClickListener {
            getCurrentUserSession()
        }

        binding.previousSessionBtn.setOnClickListener {
            getPreviousUserSession()
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentUserSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.userSessionData.removeObservers(this)
        mainViewModel.previousUserSessionData.removeObservers(this)
        mainViewModel.logoutEvent.removeObservers(this)
        mainViewModel.lockscreenStatusStatePermission.removeObservers(this)

        mainViewModel.releaseEMDKManager()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ms_auth -> {
                startActivity(Intent(this@MainActivity, MSSignInActivity::class.java))
                return true
            }

            R.id.action_authenticate -> {
                startActivity(Intent(this@MainActivity, UserAuthenticationActivity::class.java))
                return true
            }

            R.id.action_logout -> {
                mainViewModel.performIGLogout()
                return true
            }

            R.id.action_start_service -> {
                mainViewModel.acquirePermissionForLockscreenStatusState()
                return true
            }

            R.id.action_set_lockscreen_message -> {
                startActivity(Intent(this@MainActivity, LockScreenMessageManagementActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getCurrentUserSession() {
        //Legacy OLD API
        //mainViewModel.getCurrentUserSessionLegacy()

        mainViewModel.getCurrentUserSession()
    }

    private fun getPreviousUserSession() {
        mainViewModel.getPreviousUserSession()
    }

    private val userSessionObserver = Observer<String> { userSessionData ->
        runOnUiThread {
            if (userSessionData.isEmpty()) {
                Toast.makeText(
                    this,
                    "Unable to get user session data, check logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }

            binding.userSession.text = userSessionData
        }
    }

    private val previousUserSessionObserver = Observer<String> { previousUserSessionData ->
        runOnUiThread {
            if (previousUserSessionData.isEmpty()) {
                Toast.makeText(
                    this,
                    "Unable to get previous user session data, check logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }

            binding.userSession.text = previousUserSessionData
        }
    }

    private val logoutEventObserver = Observer<Boolean> { state ->
        runOnUiThread {
            if (!state) {
                Toast.makeText(
                    this,
                    "Unable to logout current user, check logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }
        }
    }

    private val lockscreenStatusStateObserver = Observer<Boolean> { state ->
        runOnUiThread {
            if (!state) {
                Toast.makeText(
                    this,
                    "Unable to grant access for the lockscreen status state permission therefore the service cannot be started, check logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}