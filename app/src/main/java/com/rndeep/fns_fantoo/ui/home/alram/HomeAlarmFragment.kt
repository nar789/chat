package com.rndeep.fns_fantoo.ui.home.alram

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentHomeAlarmBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.CustomDividerDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeAlarmFragment() :
    BaseFragment<FragmentHomeAlarmBinding>(FragmentHomeAlarmBinding::inflate) {

    private val alarmViewModel: HomeAlarmViewModel by viewModels()

    private val homeAlarmAdapter = HomeAlarmAdapter()

    private lateinit var dividerDecoRation: CustomDividerDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dividerDecoRation =
            CustomDividerDecoration(1f, 20f, requireContext().getColor(R.color.gray_400_opacity12), true)

        activity?.let {
            activity?.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }

    }

    override fun initUi() {
        settingObserver()

        initToolbar()
        binding.rcAlarm.layoutManager = LinearLayoutManager(context)
        binding.rcAlarm.adapter = homeAlarmAdapter
        binding.rcAlarm.addSingleItemDecoRation(dividerDecoRation)

        alarmViewModel.getAlarmList()

    }

    override fun initUiActionEvent() {
        binding.alarmToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.alarm_read_all -> {
                    alarmViewModel.callAllReadAlarm()
                    return@setOnMenuItemClickListener true
                }


                else -> false
            }

        }


        homeAlarmAdapter.setOnAlarmClickListener(object : HomeAlarmAdapter.AlarmClickListener {
            override fun onAlarmClick(alarmId: Int, position: Int, isRead: Boolean) {
                homeAlarmAdapter.changeRead(position)
            }

        })

    }


    private fun initToolbar() {
        binding.alarmToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun settingObserver() {
        alarmViewModel.alarmErrorData.observe(viewLifecycleOwner){
            activity?.showErrorSnackBar(binding.root,it)
        }

        alarmViewModel.alarmListLiveData.observe(viewLifecycleOwner) {
            var isAllRead = true
            for (a in it) {
                if (a.isRead != true) {
                    isAllRead = false
                    break
                }
            }
            if (isAllRead) {
                changeMenuTextColor(requireContext().getColor(R.color.gray_300))
            } else {
                changeMenuTextColor(requireContext().getColor(R.color.primary_500))
            }
            homeAlarmAdapter.setItems(it)
            if(it.isEmpty()){
                binding.emptyContainer.visibility = View.VISIBLE
            }
        }
        //모두 읽음 결과
        alarmViewModel.allReadResultLiveData.observe(viewLifecycleOwner) {
            if (it) {
                homeAlarmAdapter.changeAllRead()
                changeMenuTextColor(requireContext().getColor(R.color.gray_300))
            }
        }

    }

    fun changeMenuTextColor(@ColorInt colorInt: Int) {
        val s = SpannableString(getString(R.string.m_read_all))
        s.setSpan(ForegroundColorSpan(colorInt), 0, s.length, 0)
        s.setSpan(AbsoluteSizeSpan(28), 0, s.length, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val font = Typeface.create(ResourcesCompat.getFont(requireContext(),R.font.noto_sans_kr),Typeface.NORMAL)
            s.setSpan(TypefaceSpan(font),0,s.length,0)
        }

        binding.alarmToolbar.menu.getItem(0).title = s
    }

}