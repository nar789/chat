package com.rndeep.fns_fantoo.ui.club.join

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubJoinBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.editor.MultimediaType
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception

/**
 *    ivBack : 뒤로가기
 *    flClubThumbnailContainer : 썸네일 클릭
 *    edtNickName : 닉네임 입력
 *    tvLengthCount : 닉네임 길이
 *    ivDuplicateCheckIcon : 중복 확인 아이콘
 *    tvCheckDuplicate : 중복확인
 *    tvJoinClubBtn : 클럽 가입버튼
 */

@AndroidEntryPoint
class ClubJoinFragment : BaseFragment<FragmentClubJoinBinding>(FragmentClubJoinBinding::inflate) {

    private val clubJoinViewModel: ClubJoinViewModel by viewModels()
    private lateinit var getImageResult: ActivityResultLauncher<Intent>
    val getItem: ClubJoinFragmentArgs by navArgs()
    var checkDuplicate = true
    var clubId: String? = null
    lateinit var profileDefaultList : String
    private var clubNickName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whiteStatusBar()
        profileDefaultList = resources.getStringArray(R.array.club_profile_default_list).toList()[(Math.random()*5).toInt()]
    }

    override fun initUi() {
        clubId = getItem.clubId
        getImageResult = getProfileImageActivityResult()
        clubJoinViewModel.settingDefaultProfileImage(profileDefaultList)
        binding.ivClubThumbnail.clipToOutline = true
        binding.tvJoinClubBtn.clipToOutline = true
        binding.tvCheckDuplicate.isEnabled = false
        clubJoinViewModel.getClubInfo(getItem.clubId)
        settingObserve()
    }

    override fun initUiActionEvent() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            //이미지 가져오기
            flClubThumbnailContainer.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                getImageResult.launch(intent)
            }

            //닉네임 Watcher
            edtNickName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    clubNickName = p0.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0 ?: return
                    if (p0.isNotEmpty()) {
                        edtNickName.background =
                            requireContext().getDrawable(R.drawable.bg_border_radius_8_c_gray50_s_primary500)
                        ivEdtCleanBtn.visibility = View.VISIBLE
                    } else {
                        edtNickName.background =
                            requireContext().getDrawable(R.drawable.bg_border_radius_8_c_gray50_s_gray100)
                        ivEdtCleanBtn.visibility = View.GONE
                    }
                    //최소 글자 기획 미정
                    tvLengthCount.text = "${p0.length}/20"
                    tvCheckDuplicate.isClickable = p0.isNotEmpty()
                    tvCheckDuplicate.isEnabled = p0.isNotEmpty()
                    tvJoinClubBtn.backgroundTintList =
                        ColorStateList.valueOf(requireContext().getColor(R.color.state_disabled_gray_200))
                    ivDuplicateCheckIcon.visibility = View.GONE
                    binding.tvNickNameCheckResult.visibility=View.GONE
                    checkDuplicate = true
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            //중복 확인
            tvCheckDuplicate.setOnClickListener {
                if (clubId == null) {
                    checkResultText(
                        requireContext().getColor(R.color.state_active_secondary_default),
                        getString(R.string.se_g_not_correct_club_infomation)
                    )
                } else if (edtNickName.text.length !in 2..20) {
                    checkResultText(
                        requireContext().getColor(R.color.state_active_secondary_default),
                        getString(R.string.se_num_write_within_2arg,2,20)
                    )
                } else if(edtNickName.text.startsWith(" ")){
                    checkResultText(
                        requireContext().getColor(R.color.state_active_secondary_default),
                        getString(R.string.se_c_cannot_use_blank_first_character)
                    )
                }else if (edtNickName.text.length in 2..20) {
                    binding.tvNickNameCheckResult.visibility=View.GONE
                    clubJoinViewModel.checkDuplicate(clubId!!, edtNickName.text.toString())
                }
            }

            //가입 신청
            tvJoinClubBtn.setOnClickListener {
                //썸네일은 필수가 아님
                if (!checkDuplicate && clubId != null) {
                    context?.let {
                        clubJoinViewModel.callClubJoinToServer(
                            clubId!!,
                            edtNickName.text.toString(),
                            it.getString(R.string.cloudFlareKey)
                        )
                    }
                } else if (checkDuplicate) {
                    activity?.showCustomSnackBar(
                        binding.root,
                        getString(R.string.se_j_do_try_after_checking_duplicate)
                    )
                } else {
                    activity?.showCustomSnackBar(
                        binding.root,
                        getString(R.string.se_g_not_correct_club_infomation)
                    )
                }
            }

            //글자 지우기
            ivEdtCleanBtn.setOnClickListener {
                edtNickName.setText("")
            }
        }

    }

    private fun settingObserve() {
        clubJoinViewModel.clubExistsLiveData.observe(this){
            if(!it){
                activity?.showErrorSnackBar(binding.root,"Error_FE3005")
                findNavController().popBackStack()
            }
        }

        clubJoinViewModel.clubDuplicateLiveData.observe(viewLifecycleOwner) {
            //false : 사용가능 , true : 사용불가
            checkDuplicate = it
            if (!it) {
                binding.tvJoinClubBtn.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.btn_active))
                binding.edtNickName.clearFocus()
                checkResultText(
                    requireContext().getColor(R.color.state_active_primary_default),
                    getString(R.string.se_s_can_use_nickname)
                )
            } else {
                checkResultText(
                    requireContext().getColor(R.color.state_active_secondary_default),
                    getString(R.string.se_a_already_use_nickname)
                )
            }
        }

        clubJoinViewModel.joinErrorLiveData.observe(viewLifecycleOwner) {
            val message = when (it) {
                ConstVariable.ERROR_FE3007 -> {
                    SpannableString(getString(R.string.Error_FE3007))
                }
                else -> {
                    SpannableString("ERROR => ${it}")
                }
            }
            CommonDialog.Builder().apply {
                title = ""
                setMessageSpannable(message)
                positiveButtonString = getString(R.string.h_confirm)
                positiveButtonClickListener = object : CommonDialog.ClickListener {
                    override fun onClick() {
                        findNavController().popBackStack()
                    }
                }
                negativeButtonClickListener = null
                negativeButtonString = ""
            }.build().show(childFragmentManager, "tag")
        }

        clubJoinViewModel.clubJoinResultLiveData.observe(viewLifecycleOwner) {
            val message: SpannableString = if(it==true){
                SpannableString(getString(R.string.se_k_joined_club_welcome, clubJoinViewModel.clubName)).apply {
                    setSpan(
                        ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                        0,
                        clubJoinViewModel.clubName.length,
                        0
                    )
                }
            }else{
                SpannableString(getString(R.string.se_k_request_joind_club_welcome, clubJoinViewModel.clubName)).apply {
                    setSpan(
                        ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                        0,
                        clubJoinViewModel.clubName.length,
                        0
                    )
                }
            }

            CommonDialog.Builder().apply {
                title = ""
                setMessageSpannable(message)
                positiveButtonString = getString(R.string.h_confirm)
                positiveButtonClickListener = object : CommonDialog.ClickListener {
                    override fun onClick() {
                        if(it==true){
                            if(clubId==null){
                                findNavController().popBackStack()
                            }else{
                                val destinationId = findNavController().previousBackStackEntry?.destination?.id
                                if(destinationId == R.id.clubTabFragment){
                                    findNavController().navigate(
                                        R.id.action_clubJoinFragment_to_club_page_detail,
                                       bundleOf("clubId" to clubId!!)
                                    )
                                }else{
                                    findNavController().popBackStack()
                                }
                            }

                        }else{
                            findNavController().popBackStack()
                        }
                    }
                }
                negativeButtonClickListener = null
                negativeButtonString = ""
            }.build().show(childFragmentManager, "tag")
        }

        clubJoinViewModel.profileDefaultImage.observe(this){
            if(it.isNullOrEmpty()) return@observe

            setProfileAvatar(binding.ivClubThumbnail,getString(R.string.imageThumbnailUrl,it))
        }
    }

    fun whiteStatusBar() {
        activity?.let {
            it.window?.statusBarColor = it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
    }

    private fun getProfileImageActivityResult() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data == null) {
                    showAlert(getString(R.string.se_a_not_available_file_format))
                    return@registerForActivityResult
                }
                val fileInfo = getFileRealInfos(data.data)
                if (fileInfo == null) {
                    showAlert(getString(R.string.se_a_not_available_file_format))
                    return@registerForActivityResult
                }
                val fileName =
                    if (fileInfo["FileName"] != null) fileInfo["FileName"].toString() else null
                val fileSize =
                    if (fileInfo["FileSize"] != null) fileInfo["FileSize"] as Long else null
                if (fileName == null || fileSize == null) {
                    showAlert(getString(R.string.se_a_not_available_file_format))
                    return@registerForActivityResult
                }
                if (fileName.substring(fileName.indexOf(".")) == ".jpg" ||
                    fileName.substring(fileName.indexOf(".")) == ".jpeg" ||
                    fileName.substring(fileName.indexOf(".")) == ".png" ||
                    fileSize <= 5000000
                ) {
                    context?.let { context ->
                        clubJoinViewModel.makeMultiFileAsUri(
                            data.data.toString(),
                            MultimediaType.IMAGE,
                            context
                        ).run {
                            if(this){
                                binding.ivClubThumbnail.imageTintList = null
                                binding.ivClubThumbnail.setPadding(0, 0, 0, 0)
                                setImageWithPlaceHolder(
                                    binding.ivClubThumbnail,
                                    data.dataString,
                                    R.drawable.profile_character_l
                                )
//                                binding.ivClubThumbnail.setImageURI(data.data)
                            }else{
                                showAlert(getString(R.string.se_a_not_available_file_format))
                            }
                        }
                    }
                } else if (fileSize > 5000000) {
                    showAlert(getString(R.string.se_a_over_file_size))
                } else {
                    showAlert(getString(R.string.se_a_over_file_size))
                }
            }

    }

    //파일 정보 확인 메소드
    @SuppressLint("Range", "Recycle")
    fun getFileRealInfos(imageUri: Uri?): HashMap<String, Any>? {
        imageUri ?: return null
        activity?.let {
            val fileInfos = HashMap<String, Any>()
            try {
                val cursor2 = it.contentResolver.query(imageUri, null, null, null, null)
                cursor2?.let {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    it.moveToFirst()
                    fileInfos["FileName"] = it.getString(nameIndex)
                    fileInfos["FileSize"] = it.getLong(sizeIndex)
                    return fileInfos

                }
            } catch (e: Exception) {
                Timber.d("Image Error : ${e.localizedMessage}")
                return null
            }
        }
        return null
    }

    private fun checkResultText(@ColorInt color :Int, resultText :String){
        binding.tvNickNameCheckResult.visibility=View.VISIBLE
        binding.tvNickNameCheckResult.setTextColor(color)
        binding.tvNickNameCheckResult.text=resultText
    }

    private fun showAlert(alertMessage: String) {
        val dialog = CommonDialog.Builder().apply {
            message = alertMessage
            positiveButtonString = "확인"
        }
        dialog.build().show(this.parentFragmentManager, "alert")
    }
}