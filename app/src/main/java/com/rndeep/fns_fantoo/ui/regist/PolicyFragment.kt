package com.rndeep.fns_fantoo.ui.regist

import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.RegistPolicyFragmentBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.Regist.Companion.KEY_POLICY_ENTRY_ROUTE_TYPE

class PolicyFragment :
    RegistFragment<RegistPolicyFragmentBinding>(RegistPolicyFragmentBinding::inflate) {
    override fun initUi() {

        //test
        //binding.webView.loadUrl("https://www.fantoo.co.kr/service")
        when (arguments?.get(KEY_POLICY_ENTRY_ROUTE_TYPE)) {
            ConstVariable.Regist.PolicyType.TYPE_USE_SERVICE -> {
                binding.topbar.setTitle(getString(R.string.s_term_use_service))
            }
            ConstVariable.Regist.PolicyType.TYPE_PRIVATE_TREATMENT -> {
                binding.topbar.setTitle(getString(R.string.g_term_privacy_info))
            }
            ConstVariable.Regist.PolicyType.TYPE_YOUTH_CARE -> {
                binding.topbar.setTitle(getString(R.string.c_term_youth))
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
    }

}