package com.rndeep.fns_fantoo.ui.club.settings

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonEditTextLayout
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingMyProfileBinding
import com.rndeep.fns_fantoo.data.local.model.CommonResult
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoPacerable
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubMyProfileViewModel
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.CloudFlare.IMAGE_OPT_THUMBNAIL
import com.rndeep.fns_fantoo.utils.ConstVariable.CloudFlare.IMAGE_PRE_URL
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_FE3015
import com.rndeep.fns_fantoo.utils.ConstVariable.ProfileDef.Companion.NICKNAME_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ProfileDef.Companion.NICKNAME_MIN_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ProfileDef.Companion.PROFILE_PHOTO_MAX_SIZE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*

@AndroidEntryPoint
class ClubSettingMyProfileSetFragment :
    ClubSettingBaseFragment<FragmentClubSettingMyProfileBinding>(FragmentClubSettingMyProfileBinding::inflate) {

    private val clubMyProfileViewModel: ClubMyProfileViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var clubMemberInfo: ClubMemberInfoPacerable

    private val takePhotoFromAlbumLauncher = // 갤러리에서 사진 가져오기
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    Timber.d("takePhotoFromAlbumLauncher uri =$uri")
                    context?.let {
                        val fileType = it.contentResolver.getType(uri)
                        Timber.d("takePhotoFromAlbumLauncher fileType = $fileType")
                        if (fileType != null) {
                            if (!(fileType.contains("jpg") || fileType.contains("jpeg")
                                        || fileType.contains("png"))
                            ) {
                                showDialog(getString(R.string.se_a_not_available_file_format))
                                return@registerForActivityResult
                            }
                        }
                        val pickPhotoFileSize = ImageUtils.getImageSizeFromUri(it, uri)
                        if (pickPhotoFileSize > PROFILE_PHOTO_MAX_SIZE || pickPhotoFileSize < 0) {
                            showDialog(getString(R.string.se_a_over_file_size))
                            return@registerForActivityResult
                        }
                    }
                    //binding.ivBadge.visibility = View.GONE
                    clubMyProfileViewModel._uploadFileImageUriLiveData.value = uri
                    setProfileAvatar(binding.ivProfile, uri.toString())
                    binding.topbar.setTailTextEnable(true)
                }
            } else if (result.resultCode != Activity.RESULT_CANCELED) {
                Timber.i("takePhoto Result canceled")
            }
        }
    private val takePhotoFromAlbumIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png")
            )
        }

    override fun initUi() {
        val args: ClubSettingMyProfileSetFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        clubMemberInfo = args.clubMemberInfo

        if (!clubMemberInfo.profileImg.isNullOrEmpty()) {
            setProfileAvatar(
                binding.ivProfile,
                ImageUtils.getImageFullUriForCloudFlare(clubMemberInfo.profileImg!!, true)
            )
        }
        clubMyProfileViewModel._currentNickNameLiveData.value = clubMemberInfo.nickname
        binding.cEtLayout.setText(clubMyProfileViewModel.currentNickNameLiveData.value!!)
        binding.cEtLayout.setButtonEnable(false)

        clubMyProfileViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }

        clubMyProfileViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubMyProfileViewModel.nickNameCheckLiveData.observe(viewLifecycleOwner) { nickNameCheckResult ->
            when (nickNameCheckResult.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    binding.topbar.setTailTextEnable(true)
                }
                else -> {
                    requireContext().showErrorSnackBar(binding.root, nickNameCheckResult.code)
                }
            }
        }

        clubMyProfileViewModel.profileImageUploadResultLiveData.observe(viewLifecycleOwner) { resultString ->
            if (!resultString.isNullOrEmpty()) {
                clubMyProfileViewModel.saveProfile(
                    clubId,
                    uid,
                    binding.cEtLayout.getText(),
                    clubMyProfileViewModel.nickNameCheckTokenLiveData.value,
                    clubMyProfileViewModel.profileImageUploadResultLiveData.value
                )
            }
        }
        clubMyProfileViewModel.saveProfileResultLiveData.observe(viewLifecycleOwner) { saveResult ->
            when (saveResult.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    clubMemberInfo.profileImg?.let {
                        if (clubMemberInfo.profileImg != clubMyProfileViewModel.profileImageUploadResultLiveData.value) {
                            //이전 프로필 이미지 삭제
                            clubMyProfileViewModel.deleteProfileImage(
                                getString(R.string.cloudFlareKey),
                                clubMemberInfo.profileImg!!
                            )
                        }
                        context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                        lifecycleScope.launch {
                            delay(500)
                            navController.popBackStack()
                        }
                    }
                }
                //{"code":"FE3015","msg":"30일에 한번 변경 가능합니다."
                ERROR_FE3015 -> {
                    setWarningMessageShow(true, getString(R.string.se_n_change_nickname_notice_2), false)
                    if (!clubMyProfileViewModel.profileImageUploadResultLiveData.value.isNullOrEmpty()) {
                        //업로드 이미지 삭제
                        clubMyProfileViewModel.deleteProfileImage(
                            getString(R.string.cloudFlareKey),
                            clubMyProfileViewModel.profileImageUploadResultLiveData.value!!
                        )
                    }
                }
                else -> {
                    requireContext().showErrorSnackBar(binding.root, saveResult.code)
                }
            }
        }
        updateNickNameLengthCountText()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(300)
            binding.cEtLayout.requestFocus()
            showSoftInput(binding.cEtLayout.getEditText())
        }
        binding.cEtLayout.setMaxLength(NICKNAME_MAX_LENGTH)

        //clubMyProfileViewModel.getProfileInfo(clubId, uid)
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            showDialog(
                "",
                getString(R.string.se_p_cancel_modify_profile),
                "",
                getString(R.string.h_confirm),
                getString(R.string.c_cancel),
                object : CommonDialog.ClickListener {
                    override fun onClick() {
                        navController.popBackStack()
                    }
                },
                null
            )
        }
        binding.topbar.setTailTextEnable(false)

        binding.topbar.setTailTextClickListener {
            val inputNickName = binding.cEtLayout.getText()
            //닉네임변경했으나 중복체크 안한 경우
            if (inputNickName != clubMyProfileViewModel.currentNickNameLiveData.value &&
                clubMyProfileViewModel.nickNameCheckTokenLiveData.value.isNullOrEmpty()
            ) {
                showDialog(getString(R.string.se_n_do_checking_duplicate_nickname))
                return@setTailTextClickListener
            }

            if (clubMyProfileViewModel.uploadFileImageUriLiveData.value != null) {
                val uri: Uri = clubMyProfileViewModel.uploadFileImageUriLiveData.value!!
                context?.let { it1 ->
                    val multipart = uri.asMultipart(
                        "file",
                        "club_${clubId}_${Date().time}_${TextUtils.getRandomString(3)}",
                        it1.contentResolver
                    )
                    if (multipart != null) {
                        clubMyProfileViewModel.uploadProfileImage(
                            getString(R.string.cloudFlareKey),
                            multipart
                        )
                    }
                }
            } else {
                clubMyProfileViewModel.saveProfile(
                    clubId,
                    uid,
                    inputNickName,
                    clubMyProfileViewModel.nickNameCheckTokenLiveData.value,
                    clubMyProfileViewModel.profileImageUploadResultLiveData.value
                )
            }
            //clubMyProfileViewModel.saveProfile(clubId, File(""), "", "")
        }

        binding.ivProfile.setOnClickListener {
            //사진 변경
            context?.showCustomSnackBar(
                binding.root, getString(
                    R.string.se_p_attach_file_size_limit,
                    getString(R.string.photo_max_size)
                )
            )
            //pickImages.launch("image/*")
            takePhotoFromAlbumLauncher.launch(takePhotoFromAlbumIntent)

        }
        binding.cEtLayout.setButtonClickListener {
            val nickStr = binding.cEtLayout.getText()
            if (nickStr.isEmpty()) {
                return@setButtonClickListener
            }

            if (nickStr[0] == ' ') {
                setWarningMessageShow(true, getString(R.string.se_c_cannot_use_blank_first_character), false)
                return@setButtonClickListener
            }
            //사용불가 문자포함
            //showDialog(getString(R.string.se_s_including_cannot_use_char))

            //길이 제한
            val inputLength = binding.cEtLayout.getText().length
            if (inputLength < NICKNAME_MIN_LENGTH || inputLength > NICKNAME_MAX_LENGTH) {
                setWarningMessageShow(
                    true, String.format(
                        getString(R.string.se_j_write_min_max_limit_length),
                        NICKNAME_MIN_LENGTH.toString(), NICKNAME_MAX_LENGTH.toString()
                    ), false
                )
                return@setButtonClickListener
            }

            //기 사용중인 명
            //showDialog(getString(R.string.se_a_already_use_nickname))

            //변경가능 기간이 아님
            /*
            val remainDate = "10"
            showDialog(String.format(getString(R.string.unavailable_change_reason_period),
                NICKNAME_CHANGE_DATE_LIMIT.toString(),
                remainDate
            ))
           */

            //사용가능
            setWarningMessageShow(
                true,
                getString(R.string.se_s_can_use_nickname),
                true
            )
            binding.topbar.setTailTextEnable(true)

            clubMyProfileViewModel.checkProfileNickName(clubId, nickStr)
        }

        binding.cEtLayout.addTextChangedListener(textWatcher())
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000)
        {
            if(resultCode == RESULT_OK)
            {
                val currentImageUri = data?.data
                Timber.d(" onActivityResult image = $currentImageUri")
            }
        }
    }*/

    fun nickCheckingObserver() = Observer<CommonResult> { }

    fun textWatcher() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            try {
                val afterNickName: String? = editable?.toString()
                var isChanged = false
                if (!clubMyProfileViewModel.currentNickNameLiveData.value.isNullOrEmpty()
                    && !afterNickName.isNullOrEmpty()
                    && afterNickName != clubMyProfileViewModel.currentNickNameLiveData.value
                ) {
                    isChanged = true
                    clubMyProfileViewModel._nickNameCheckTokenLiveData.value = ""
                    if (clubMyProfileViewModel.uploadFileImageUriLiveData.value == null) {
                        binding.topbar.setTailTextEnable(false)
                    }
                }
                binding.cEtLayout.setButtonEnable(isChanged)
            } catch (e: Exception) {
                Timber.e("afterTextChanged err ${e.message}")
            }
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateNickNameLengthCountText()
            setWarningMessageShow(false, "", false)
        }
    }

    private fun setWarningMessageShow(isShow: Boolean, message: String, stateEnable: Boolean) {
        binding.tvWarning.text = message
        binding.tvWarning.isEnabled = stateEnable
        binding.tvWarning.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.tvNormalDesc.visibility = if (isShow) View.GONE else View.VISIBLE
        if (stateEnable) {
            binding.cEtLayout.setInputState(CommonEditTextLayout.EditorState.SUCCESS)
        }
    }

    private fun updateNickNameLengthCountText() {
        if (binding.cEtLayout.getText().isEmpty()) {
            binding.tvNickWordCount.text =
                binding.cEtLayout.getText().length.toString() + getString(R.string.slash) + NICKNAME_MAX_LENGTH
        } else {
            context?.let {
                TextUtils.applyParticalColor(
                    binding.cEtLayout.getText().length.toString(),
                    getString(R.string.slash) +
                            NICKNAME_MAX_LENGTH,
                    it.getColor(R.color.primary_default),
                    binding.tvNickWordCount,
                    true
                )
            }
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}