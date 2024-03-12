package com.zebra.nilac.igplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

open class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    protected fun showLoadingScreen() {
        if (loadingDialog != null) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)

        loadingDialog = LoadingDialogFragment()
        loadingDialog?.isCancelable = false

        loadingDialog?.show(transaction, LoadingDialogFragment::class.java.name)
    }

    protected fun dismissLoadingScreen() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}