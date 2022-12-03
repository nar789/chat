package com.rndeep.fns_fantoo.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ActivityLoginMainBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = (supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment).navController
        val navStartDestinationID = intent.getIntExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, -1)
        val graph = navController.navInflater.inflate(R.navigation.login)
        if(navStartDestinationID == -1){
            graph.setStartDestination(R.id.loginSplashFragment)
        }else{
            graph.setStartDestination(navStartDestinationID)
        }
        navController.setGraph(graph, intent.extras)
        intent.removeExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID)
    }

    interface PermissionsResultListener{
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        )
    }
    lateinit var callbackPermissionResultListener:PermissionsResultListener

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionsResult  $requestCode , grantResults = $grantResults")
        callbackPermissionResultListener.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}