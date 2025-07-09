package com.zebra.nilac.igplayground.ui.lockscreen

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.zebra.nilac.igplayground.AppConstants
import com.zebra.nilac.igplayground.databinding.LockscreenMessageManagementBinding
import com.zebra.nilac.igplayground.databinding.UserAuthenticationBinding
import com.zebra.nilac.igplayground.ui.BaseActivity

class LockScreenMessageManagementActivity : BaseActivity() {

    private lateinit var binding: LockscreenMessageManagementBinding
    private val lockScreenMessageManagementViewModel: LockScreenMessageManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LockscreenMessageManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Set Lockscreen Message")
        setSupportActionBar(binding.toolbar)

        binding.setMessageBtn.setOnClickListener {
            setLockscreenMessage()
        }

        lockScreenMessageManagementViewModel.lockscreenMessagePermission.observe(
            this,
            lockscreenMessagePermissionObserver
        )

        showLoadingScreen()
        lockScreenMessageManagementViewModel.acquirePermissionForLockscreenMessageSupport()
    }

    override fun onDestroy() {
        super.onDestroy()
        lockScreenMessageManagementViewModel.lockscreenMessagePermission.removeObservers(this)
        lockScreenMessageManagementViewModel.releaseEMDKManager()
    }

    private fun setLockscreenMessage() {
        val messageBundle = Bundle().apply {
            putString("Title", binding.messageTitle.text.toString())
            putString("Message", binding.messageSummary.text.toString())
            putString("Type", binding.messageType.text.toString())
            putInt("Timeout", binding.messageTimeout.text.toString().toInt())
            putBoolean("Dismissible", binding.dismissibleSwitch.isChecked)
        }

        val callResponse = contentResolver.call(
            Uri.parse(AppConstants.BASE_URI),
            AppConstants.LOCKSCREEN_ACTION,
            AppConstants.LOCKSCREEN_SET_MESSAGE_METHOD,
            messageBundle
        )

        if (callResponse != null && callResponse.containsKey("RESULT")) {
            val resultMessage = callResponse.getString("RESULT")
            Log.i(TAG, callResponse.getString("RESULT")!!)

            if (resultMessage.equals("SUCCESS")) {
                Toast.makeText(
                    this,
                    "New lockscreen message has been successfully set",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.i(TAG, "Data Received: ${callResponse.getString("DATA")}")
                Toast.makeText(
                    this,
                    "Setting new lockscreen message with status: $resultMessage, check logs for more details",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private val lockscreenMessagePermissionObserver = Observer<Boolean> { status ->
        runOnUiThread {
            dismissLoadingScreen()
            if (!status) {
                Toast.makeText(
                    this,
                    "Unable to grant required permission to set lockscreen message, check the logs",
                    Toast.LENGTH_LONG
                ).show()
                return@runOnUiThread
            }
        }
    }

    companion object {
        const val TAG = "LockScreenMessageManagementActivity"
    }
}