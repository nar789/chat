package com.rndeep.fns_fantoo.ui.club.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubNoticeBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.checkLastItemVisible
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import com.rndeep.fns_fantoo.utils.setWhiteStatusBarIcon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClubNoticeFragment : Fragment() {

    private var _binding :FragmentClubNoticeBinding? =null
    val binding get() = _binding!!

    private val viewModel: ClubNoticeViewModel by viewModels()

    private val noticeArg :ClubNoticeFragmentArgs by navArgs()

    private val noticeAdapter = ClubNoticeAdapter()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.setDarkStatusBarIcon()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_club_notice, container, false)
        _binding= FragmentClubNoticeBinding.bind(view)

        initToolbar()
        settingObserver()
        binding.rcNoticeList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.rcNoticeList.adapter=noticeAdapter.apply {
            setClubNoticeListener(object :ClubNoticeAdapter.ClubNoticeClickListener{
                override fun onItemClick(postId: Int, clubId: String, categoryCode: String) {
                    findNavController().navigate(
                        ClubNoticeFragmentDirections.actionClubNoticeFragmentToClubPost(
                            ConstVariable.TYPE_CLUB_NOTICE,categoryCode,clubId, postId
                        )
                    )
                }

                override fun onOptionClick(postId: Int) {

                }
            })
        }
        viewModel.getClubNoticeList(noticeArg.clubId)
        return binding.root
    }

    fun initToolbar() {
        //뒤로가기
        binding.noticeToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun settingObserver(){
        viewModel.clubNoticePostLiveData.observe(viewLifecycleOwner){
            noticeAdapter.submitList(it)
            binding.fantooLoadingView.visibility=View.GONE
        }
    }

    fun detectRecyclerviewBottom() {
        binding.rcNoticeList.checkLastItemVisible {
            if (noticeAdapter.itemCount < 5) {
                return@checkLastItemVisible
            }
            if (it && !isLoading) {
                isLoading = true

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        activity?.setWhiteStatusBarIcon()
    }
}