package com.rndeep.fns_fantoo.ui.menu.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.databinding.FragmentPushNotificationSettingBinding
import com.rndeep.fns_fantoo.ui.menu.MenuItem
import com.rndeep.fns_fantoo.ui.menu.MenuListAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Push Notification Setting UI
 */
@AndroidEntryPoint
class PushNotificationSettingFragment : Fragment() {

    lateinit var binding: FragmentPushNotificationSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    private lateinit var pushNotificationAdapter: MenuListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        pushNotificationAdapter = MenuListAdapter() { item ->
            Timber.d("clicked item : $item")
            val menuItem = item as MenuItem.SwitchItem

        }

        binding = FragmentPushNotificationSettingBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            pushNotificationMenuList.run {
                adapter = pushNotificationAdapter
                setHasFixedSize(true)
                itemAnimator = null
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pushNotificationMenu.observe(viewLifecycleOwner) { list ->
            pushNotificationAdapter.submitList(list)
        }
    }
}