package com.zebra.nilac.igplayground.ui.ms

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.common.java.authorities.Authority.KnownAuthorityResult
import com.zebra.nilac.igplayground.R
import com.zebra.nilac.igplayground.databinding.ActivityMainBinding
import com.zebra.nilac.igplayground.databinding.MsSigninAcitivityBinding
import com.zebra.nilac.igplayground.ui.BaseActivity

class MSSignInActivity : BaseActivity() {

    private lateinit var binding: MsSigninAcitivityBinding

    private var msalApp: ISingleAccountPublicClientApplication? = null
    private val scopes = arrayOf("User.Read")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MsSigninAcitivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Microsoft Authentication Check")
        setSupportActionBar(binding.toolbar)

        binding.msSigninBtn.setOnClickListener {
            signInInteractive()
        }

        binding.msLogoutBtn.setOnClickListener {
            logOutUser()
        }

        PublicClientApplication.createSingleAccountPublicClientApplication(
            applicationContext,
            R.raw.msal_auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(app: ISingleAccountPublicClientApplication) {
                    msalApp = app

                    //Silently check if we already have an active account and session
                    showLoadingScreen()
                    checkForExistingSession()
                }

                override fun onError(exception: MsalException) {
                    Log.i(TAG, "MSAL init failed: ${exception.message}")
                }
            }
        )
    }

    private fun checkForExistingSession() {
        msalApp?.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(account: IAccount?) {
                if (account != null) {
                    acquireTokenSilently(account)
                } else {
                    dismissLoadingScreen()
                }
            }

            override fun onAccountChanged(prior: IAccount?, current: IAccount?) {}
            override fun onError(exception: MsalException) {
                Log.e(TAG, "Error checking account: ${exception.message}")
                dismissLoadingScreen()

                Toast.makeText(
                    this@MSSignInActivity,
                    "Error checking account, see logs",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun acquireTokenSilently(account: IAccount) {
        val params = AcquireTokenSilentParameters.Builder()
            .forAccount(account)
            .fromAuthority(account.authority)
            .withScopes(scopes.toList())
            .withCallback(object : SilentAuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    dismissLoadingScreen()
                    fillAccountInfo(result)
                }

                override fun onError(e: MsalException) {
                    Log.e(TAG, "Silent login failed: ${e.message}")
                    dismissLoadingScreen()

                    Toast.makeText(
                        this@MSSignInActivity,
                        "Silent login failed, check logs",
                        Toast.LENGTH_LONG
                    ).show()
                    signInInteractive()
                }
            })
            .build()

        msalApp?.acquireTokenSilentAsync(params)
    }

    private fun signInInteractive() {
        val signInParameters = SignInParameters.builder()
            .withActivity(this)
            .withScopes(scopes.toList())
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(result: IAuthenticationResult) {
                    fillAccountInfo(result)
                }

                override fun onError(e: MsalException) {
                    Log.e(TAG, "Login failed: ${e.message}")

                    Toast.makeText(
                        this@MSSignInActivity,
                        "Login failed, check logs",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCancel() {}
            })
            .build()

        msalApp?.signIn(signInParameters)
    }

    private fun logOutUser() {
        msalApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                binding.msSigninBtn.visibility = View.VISIBLE

                binding.accountInfoContainer.visibility = View.GONE
                binding.msLogoutBtn.visibility = View.GONE
            }

            override fun onError(e: MsalException) {
                Log.e(TAG, "Logout failed: ${e.message}")

                Toast.makeText(
                    this@MSSignInActivity,
                    "Logout failed, check logs",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun fillAccountInfo(result: IAuthenticationResult) {
        binding.msSigninBtn.visibility = View.GONE
        binding.accountInfoContainer.visibility = View.VISIBLE

        binding.msLogoutBtn.visibility = View.VISIBLE

        val account = result.account
        val tokenSnippet = result.accessToken.take(20) + "..."
        val expires = result.expiresOn

        binding.username.text = "\uD83D\uDC64 Username:\n${account.username}"
        binding.accountId.text = "\uD83C\uDD94 Account ID:\n${account.id}"
        binding.token.text = "\uD83D\uDD10 Token:\n$tokenSnippet"
        binding.tokenExpiringDate.text = "⏳ Expires:\n$expires"
    }

    companion object {
        const val TAG = "MSSignInActivity"
    }
}