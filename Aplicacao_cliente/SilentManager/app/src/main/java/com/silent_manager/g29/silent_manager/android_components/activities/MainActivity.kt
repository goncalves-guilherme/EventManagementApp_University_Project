package com.silent_manager.g29.silent_manager.android_components.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.silent_manager.g29.silent_manager.R
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.content.Intent
import android.os.Build
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.android_services.ForegroundTest
import com.silent_manager.g29.silent_manager.android_components.fragments.*
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.android_components.managers.TokenManager
import com.silent_manager.g29.silent_manager.android_components.managers.WifiManager
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    fun startTestingForegroundService(){
        val pendingIntent: Intent = Intent(this, ForegroundTest::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(pendingIntent)
        } else{
            startService(pendingIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val initialFragment: Fragment? = getFragmentInstanceById(R.id.home_menu_item)

        initialFragment?.let {
            initFragmentContainer(it)
        }
        //startTestingForegroundService()
        configureNavigationMenu()
    }

    private fun wasAppAlreadyOpened(): Boolean {
        val app = this.applicationContext as SilentManagerApplication

        return app.sharedPreferences
            .getBooleanData(SilentManagerApplication.APP_ALREADY_OPENED_PREFERENCES)
    }

    private fun setAppAlreadyOpenedForTheFirstTime(openForTheFirstTime: Boolean){
        val app = this.applicationContext as SilentManagerApplication
        app.sharedPreferences.putBooleanData(SilentManagerApplication.APP_ALREADY_OPENED_PREFERENCES, openForTheFirstTime)
    }

    private fun initFragmentContainer(initialFragment: Fragment) {
        changeFragmentContext(initialFragment)
    }

    private fun configureNavigationMenu() {
        navigation_view.setOnNavigationItemSelectedListener(::onNavigationMenuItemSelected)
    }

    private fun onNavigationMenuItemSelected(item: MenuItem): Boolean {
        val selectedFragment: Fragment? = getFragmentInstanceById(item.itemId)

        if (selectedFragment != null) {
            changeFragmentContext(selectedFragment)
            return true
        }

        return false
    }

    private fun checkLocationPermissions(): Boolean{

        val lm = LocationManager.getInstance()
        return !lm.isLocationPermitted(this)
    }

    private fun conditionsToShowScreen(fragmentTAG: String): Fragment?{
        if(checkLocationPermissions()){
            goToLocationPermissionView()
            return null
        }

        if(!wasAppAlreadyOpened()){
            setAppAlreadyOpenedForTheFirstTime(true)
            return PreferencesFragment()
        }

        return when(fragmentTAG){
            HomeFragment.HOME_FRAGMENT_TAG -> {
                val lm = LocationManager.getInstance()
                val locationEnable = lm.isLocationEnable(this)

                if(!locationEnable){
                    return RequestLocationFragment()
                }

                val wm = WifiManager.getInstance()

                if(!wm.isInternetEnable(this)){
                    return RequestInternetConnectionFragment()
                }

                return HomeFragment()

            }
            SearchFragment.SEARCH_FRAGMENT_TAG -> {
                val wm = WifiManager.getInstance()

                if (!wm.isInternetEnable(this)) {
                    return RequestInternetConnectionFragment()
                }

                return SearchFragment()
            }
            ManagementEventsFragment.MANAGMENT_EVENTS_FRAGMENT_TAG -> {
                val wm = WifiManager.getInstance()

                if (!wm.isInternetEnable(this)) {
                    return RequestInternetConnectionFragment()
                }

                if(!isUserAuthenticated()){
                    return LogInFragment()
                }

                return ManagementEventsFragment()
            }
            else -> {
                null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getFragmentInstanceById(fragmentId: Int): Fragment? =
        when (fragmentId) {
            R.id.home_menu_item -> conditionsToShowScreen(HomeFragment.HOME_FRAGMENT_TAG)
            R.id.search_menu_item -> conditionsToShowScreen(SearchFragment.SEARCH_FRAGMENT_TAG)
            R.id.management_menu_item -> conditionsToShowScreen(ManagementEventsFragment.MANAGMENT_EVENTS_FRAGMENT_TAG)
            R.id.preferences_menu_item -> PreferencesFragment()
            else -> null
        }

    private fun isUserAuthenticated(): Boolean {
        val tm = TokenManager.getInstance()
        val token = tm.getAccessToken(this)
        return !token.isNullOrEmpty()
    }

    private fun changeFragmentContext(selectedFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_container, selectedFragment)

        transaction.commit()
    }

    private fun goToLocationPermissionView() {
        val intent = Intent(applicationContext, LocationPermissionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
