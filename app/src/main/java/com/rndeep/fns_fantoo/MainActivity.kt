package com.rndeep.fns_fantoo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.rndeep.fns_fantoo.databinding.ActivityMainBinding
import com.rndeep.fns_fantoo.ui.club.ClubTabFragment
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.community.CommunityTabFragment
import com.rndeep.fns_fantoo.ui.home.HomeTabFragment
import com.rndeep.fns_fantoo.ui.regist.CongrationDialog
import com.rndeep.fns_fantoo.utils.ConstVariable.INTENT.Companion.EXTRA_REGISTER_USER
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnApplyWindowInsetsListener(null)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.menu, R.id.home, R.id.community, R.id.club,R.id.chatting)
        )

        setNavigationBar()
        setBottomNavigationVisible()
        reSelectedTab()

        intent?.let {
            if(it.getBooleanExtra(EXTRA_REGISTER_USER, false)){
                showRegistCongrationDialog()
            }
        }
    }

    private fun setNavigationBar() {
        val decorView = window.decorView
        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, decorView)
        windowInsetsControllerCompat.isAppearanceLightNavigationBars = true
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private fun setBottomNavigationVisible() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val visibility = if (destination.id == R.id.menuFragment ||
                destination.id == R.id.homeTabFragment ||
                destination.id == R.id.communityTabFragment ||
                destination.id == R.id.chatListFragment ||
                destination.id == R.id.clubTabFragment) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.bottomNavigation.visibility = visibility
        }
    }

    private fun reSelectedTab() {
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    (navHostFragment.childFragmentManager.fragments.first() as HomeTabFragment).scrollToUp()
                }
                R.id.community -> {
                    (navHostFragment.childFragmentManager.fragments.first() as CommunityTabFragment).scrollTop()
                }
                R.id.club -> {
                    (navHostFragment.childFragmentManager.fragments.first() as ClubTabFragment).scrollTop()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    //뒤로가기 처리
    private var backKeyPressedTime: Long = 0
    private val finishDelayTime = 2000
    lateinit var toast: Toast
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.clubDetailPageFragment) {
            super.onBackPressed()
        } else if (navController.navigateUp()) {
            return
        } else if (System.currentTimeMillis() - backKeyPressedTime > finishDelayTime) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, getString(R.string.h_exit_one_more_press), Toast.LENGTH_SHORT)
            toast.show()
        } else {
            toast.cancel()
            finishAffinity()
        }
    }

    private fun showRegistCongrationDialog(){
        val dialogBuilder = CongrationDialog.Builder()
        dialogBuilder.title(getString(R.string.p_profile_complete))
        dialogBuilder.titleOption(getString(R.string.en_skip))
        dialogBuilder.message(getString(R.string.se_p_congratulation_join))
        dialogBuilder.tailMessage(getString(R.string.se_n_congratulation_do_set_profile)+"\n\n"+String.format(getString(R.string.se_p_when_complete_profile_give), "500 FANiT")) //인자값 확인필요
        dialogBuilder.setPositiveButtonText(getString(R.string.se_a_go_for_complete))
        dialogBuilder.setPositiveButtonClickListener(object : CommonDialog.ClickListener{
            override fun onClick() {
                navController.navigate(R.id.editProfileFragment)
            }
        })
        val dialog = dialogBuilder.build()
        dialog.show(supportFragmentManager, "")
    }
}
