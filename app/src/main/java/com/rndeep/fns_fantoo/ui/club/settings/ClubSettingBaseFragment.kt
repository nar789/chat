package com.rndeep.fns_fantoo.ui.club.settings

import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubSettingViewModel
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.Inflate
import com.rndeep.fns_fantoo.utils.setStatusBar

abstract class ClubSettingBaseFragment<viewBinding: ViewBinding>(inflate:Inflate<viewBinding>)
    : BaseFragment<viewBinding>(inflate){
    protected val viewModel by activityViewModels<ClubSettingViewModel>()

}