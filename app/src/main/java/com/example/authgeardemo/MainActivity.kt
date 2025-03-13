package com.example.authgeardemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.authgeardemo.databinding.ActivityMainBinding
import com.oursky.authgear.AuthenticateOptions
import com.oursky.authgear.Authgear
import com.oursky.authgear.OnAuthenticateListener
import com.oursky.authgear.OnConfigureListener
import com.oursky.authgear.OnFetchUserInfoListener
import com.oursky.authgear.OnLogoutListener
import com.oursky.authgear.Page
import com.oursky.authgear.UserInfo
import com.oursky.authgear.configure
import com.oursky.authgear.logout

class MainActivity : AppCompatActivity() {

    private lateinit var authgear: Authgear
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.progressBar.visibility =  View.VISIBLE

        authgear = Authgear(application, Constants.AUTHGEAR_CLIENT_ID, Constants.AUTHGEAR_ENDPOINT)
        authgear.configure(object : OnConfigureListener {
            override fun onConfigured() {
                // Authgear can be used.
                binding.progressBar.visibility =  View.INVISIBLE
            }

            override fun onConfigurationFailed(throwable: Throwable) {
                binding.progressBar.visibility =  View.INVISIBLE
                Log.d("TAG", throwable.toString())
                // Something went wrong, check the client ID or endpoint.
            }
        })

        binding.loginBtn.setOnClickListener {
            startLogin()
        }
        binding.userSettingsBtn.setOnClickListener {
            openUserSettings()
        }
        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }

    fun startLogin() {
        binding.progressBar.visibility =  View.VISIBLE
        val options = AuthenticateOptions(Constants.AUTHGEAR_REDIRECT_URI)
        authgear.authenticate(options, object : OnAuthenticateListener {
            override fun onAuthenticated(userInfo: UserInfo) {

                updateUi(authgear)
            }

            override fun onAuthenticationFailed(throwable: Throwable) {
                binding.progressBar.visibility =  View.INVISIBLE

                Log.d("TAG", throwable.toString())
            }


        })
    }

    fun updateUi(authgear: Authgear) {
        val state = authgear.sessionState
        if (state.toString() == ("AUTHENTICATED")) {
            binding.loginBtn.visibility = View.GONE
            binding.loggedInViews.visibility = View.VISIBLE
            // Get userInfo and display in welcome text
            authgear.fetchUserInfo(object: OnFetchUserInfoListener {
                override fun onFetchedUserInfo(userInfo: UserInfo) {
                    binding.welcomeText.text = userInfo.email
                }

                override fun onFetchingUserInfoFailed(throwable: Throwable) {
                    Log.d("TAG", "Failed to fetch UserInfo")
                }

            })
        } else {
            binding.loggedInViews.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
        }
        binding.progressBar.visibility =  View.INVISIBLE
    }

    fun openUserSettings() {
        authgear.open(Page.SETTINGS)
    }

    fun logout() {
        binding.progressBar.visibility =  View.VISIBLE
        authgear.logout(true, object: OnLogoutListener {
            override fun onLogout() {
                updateUi(authgear)
            }

            override fun onLogoutFailed(throwable: Throwable) {
                Log.d("TAG", throwable.toString())
            }

        })
    }
}