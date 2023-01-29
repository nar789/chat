package com.rndeep.fns_fantoo.ui.regist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.RegistActivityBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.utils.ConstVariable.INTENT.Companion.EXTRA_FROM_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegistActivity : AppCompatActivity() {
    private lateinit var binding: RegistActivityBinding

    //private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController =
            (supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment).navController

        val callerActivity = intent.getStringExtra(EXTRA_FROM_ACTIVITY)
        Timber.d("FROM_ACTIVITY = $callerActivity")
        val graph = navController.navInflater.inflate(R.navigation.regist)
        if (!callerActivity.isNullOrEmpty() && callerActivity == LoginMainActivity::class.java.simpleName) {
            graph.setStartDestination(R.id.agreeConfirmFragment)
        } else {
            graph.setStartDestination(R.id.inputEmailFragment)
        }
        navController.setGraph(graph, intent.extras)
        //navController.graph = graph
    }

}