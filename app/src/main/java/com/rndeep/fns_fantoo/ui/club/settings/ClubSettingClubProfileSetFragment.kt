package com.rndeep.fns_fantoo.ui.club.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubAllInfoPacerable
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingProfileBinding
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubProfileSetViewModel
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonEditTextLayout
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_INTRODUCE_LENGTH_LIMIT
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_NAME_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_NAME_MIN_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_PHOTO_MAX_SIZE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_BG_IMAGE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_PROFILE_IMAGE
import com.rndeep.fns_fantoo.utils.ConstVariable.ERROR_FE3015
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.util.*

@AndroidEntryPoint
class ClubSettingClubProfileSetFragment
    :
    ClubSettingBaseFragment<FragmentClubSettingProfileBinding>(FragmentClubSettingProfileBinding::inflate) {

    private val clubProfileSetViewModel: ClubProfileSetViewModel by viewModels()
    private lateinit var clubAllInfo: ClubAllInfoPacerable
    private lateinit var clubId: String
    private lateinit var uid: String

    override fun initUi() {
        val args: ClubSettingClubProfileSetFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        clubAllInfo = args.clubAllInfo

        clubProfileSetViewModel._currentClubNameLiveData.value =
            clubAllInfo.clubName

        setProfileAvatar(
            binding.ivProfileThumbnail,
            ImageUtils.getImageFullUriForCloudFlare(clubAllInfo.profileImg, true)
        )
        if (clubAllInfo.bgImg.isNotEmpty()) {
            context?.let { context ->
                Glide.with(context)
                    .load(ImageUtils.getImageFullUriForCloudFlare(clubAllInfo.bgImg, false))
                    .into(binding.ivClubBackgroundImage)
                binding.ivDefaultIcon.visibility = View.GONE
            }
        }
        binding.cEtLayout.setText(clubProfileSetViewModel.currentClubNameLiveData.value!!)
        binding.cEtLayout.setButtonEnable(false)
        binding.etClubIntroduce.setText(clubAllInfo.introduction)
        binding.topbar.setTailTextEnable(false)
        updateClubNameLengthCountText()
        updateClubIntroduceLengthCountText()
        lifecycleScope.launchWhenCreated {
            delay(300)
            binding.cEtLayout.requestFocus()
            showSoftInput(binding.cEtLayout.getEditText())
        }
        binding.cEtLayout.setMaxLength(CLUB_NAME_MAX_LENGTH)

        clubProfileSetViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubProfileSetViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubProfileSetViewModel.clubNameCheckLiveData.observe(viewLifecycleOwner) { clubCheckNameResponse ->
            try {
                clubCheckNameResponse?.let {
                    if (clubCheckNameResponse.code == ConstVariable.RESULT_SUCCESS_CODE) {
                        val clubCheckNameDto = clubCheckNameResponse.dataObj
                        if (clubCheckNameDto != null) {
                            if (!clubCheckNameDto.existYn) {
                                //사용가능
                                setClubNameWarningText(
                                    true,
                                        getString(R.string.se_s_use_possible_club_name),
                                    true
                                )
                                checkingTopbarTailButtonEnable()
                            } else {
                                setClubNameWarningText(
                                    true,
                                    getString(R.string.se_a_already_used_club_name),
                                    false
                                )
                            }
                        }
                    } else {
                        requireContext().showErrorSnackBar(binding.root, clubCheckNameResponse.code)
                    }
                }
            } catch (e: Exception) {
                Timber.e("" + e.printStackTrace())
            }
        }

        //저장
        clubProfileSetViewModel.clubBgImageUploadResultLiveData.observe(viewLifecycleOwner) { resultString ->
            if (!resultString.isNullOrEmpty()) {
                if (clubProfileSetViewModel.clubProfileImageUriLiveData.value != null) {
                    saveProfileImage()
                } else {
                    saveClubAllInfo()
                }
            }
        }

        clubProfileSetViewModel.clubProfileImageUploadResultLiveData.observe(viewLifecycleOwner) { resultString ->
            if (!resultString.isNullOrEmpty()) {
                saveClubAllInfo()
            }
        }

        clubProfileSetViewModel.clubAllInfoSaveResultLiveData.observe(viewLifecycleOwner) { clubAllInfoSaveResponse ->
            if (clubAllInfoSaveResponse != null) {
                when (clubAllInfoSaveResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        if (clubProfileSetViewModel.clubBgImageUploadResultLiveData.value != null &&
                            clubAllInfo.bgImg != clubProfileSetViewModel.clubBgImageUploadResultLiveData.value) {
                            clubProfileSetViewModel.deleteCloudFlareImage(
                                getString(R.string.cloudFlareKey),
                                clubAllInfo.bgImg
                            )
                        }
                        if (clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value != null &&
                            clubAllInfo.profileImg != clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value) {
                            clubProfileSetViewModel.deleteCloudFlareImage(
                                getString(R.string.cloudFlareKey),
                                clubAllInfo.profileImg
                            )
                        }
                        context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                        lifecycleScope.launch {
                            delay(500)
                            navController.popBackStack()
                        }
                    }
                    ERROR_FE3015 -> {
                        if (!clubProfileSetViewModel.clubBgImageUploadResultLiveData.value.isNullOrEmpty()) {
                            clubProfileSetViewModel.deleteCloudFlareImage(
                                getString(R.string.cloudFlareKey),
                                clubProfileSetViewModel.clubBgImageUploadResultLiveData.value!!
                            )
                        }
                        if (!clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value.isNullOrEmpty()) {
                            clubProfileSetViewModel.deleteCloudFlareImage(
                                getString(R.string.cloudFlareKey),
                                clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value!!
                            )
                        }
                        setClubNameWarningText(
                            true,
                            getString(R.string.se_k_can_change_club_name),
                            false
                        )
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, clubAllInfoSaveResponse.code)
                    }
                }
            }
        }

        //clubProfileSetViewModel.getClubAllInfo(clubId, uid)
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
        binding.topbar.setTailTextClickListener {
            if (clubAllInfo.clubName != binding.cEtLayout.getText() &&
                (clubProfileSetViewModel.clubNameCheckLiveData.value == null ||
                        clubProfileSetViewModel.clubNameCheckLiveData.value?.dataObj?.existYn == true)
            ) {
                showDialog(getString(R.string.se_n_do_checking_duplicate_nickname))
                return@setTailTextClickListener
            }

            if (clubProfileSetViewModel.clubBgImageUriLiveData.value != null) {
                val uri: Uri = clubProfileSetViewModel.clubBgImageUriLiveData.value!!
                context?.let { it1 ->
                    val multipart = uri.asMultipart(
                        "file",
                        "club_${clubId}_${Date().time}_${TextUtils.getRandomString(3)}",
                        it1.contentResolver
                    )
                    if (multipart != null) {
                        clubProfileSetViewModel.uploadBgImage(
                            getString(R.string.cloudFlareKey),
                            multipart
                        )
                    }
                }
            } else if (clubProfileSetViewModel.clubProfileImageUriLiveData.value != null) {
                saveProfileImage()
            } else {
                saveClubAllInfo()
            }
        }

        //클럽배경이미지
        binding.ivClubBackgroundImage.setOnClickListener {
            context?.showCustomSnackBar(
                binding.root,
                getString(R.string.se_p_attach_file_size_limit, getString(R.string.photo_max_size))
            )
            takePhotoFromAlbumLauncherForClubBackgroundImage.launch(takePhotoFromAlbumIntent)
        }

        //클럽 프로필이미지
        binding.ivProfileThumbnail.setOnClickListener {
            context?.showCustomSnackBar(
                binding.root,
                getString(R.string.se_p_attach_file_size_limit, getString(R.string.photo_max_size))
            )
            takePhotoFromAlbumLauncherForClubProfileImage.launch(takePhotoFromAlbumIntent)
        }

        //중복확인
        binding.cEtLayout.setButtonClickListener {
            //사용불가 문자포함
            //showDialog(getString(R.string.se_s_including_cannot_use_char))

            //첫글자 공백제한
            val inputText = binding.cEtLayout.getText()
            if (inputText.isNotEmpty() && inputText[0] == ' ') {
                setClubNameWarningText(true, getString(R.string.se_c_cannot_use_blank_first_character), false)
                return@setButtonClickListener
            }

            //길이 제한
            val inputLength = inputText.length
            if (inputLength < CLUB_NAME_MIN_LENGTH || inputLength > CLUB_NAME_MAX_LENGTH) {
                setClubNameWarningText(
                    true,
                    String.format(
                        getString(R.string.se_j_write_min_max_limit_length),
                        CLUB_NAME_MIN_LENGTH.toString(), CLUB_NAME_MAX_LENGTH.toString()
                    ), false
                )
                return@setButtonClickListener
            }

            //기 사용중인 클럽명
            //setClubNameWarningText(true, String.format(getString(R.string.se_a_already_use_nickname), getString(R.string.club_name)), false)

            //변경가능 기간이 아님
            /*val availableDate = "2022-10-10"
            val convertDate = SimpleDateFormat("yyyy-MM-dd").parse(availableDate)
            val output = SimpleDateFormat("yyyy.MM.dd")
            val result = output.format(convertDate)
            setClubNameWarningText(true,
                String.format(getString(R.string.unavailable_change_reason_period),
                    getString(R.string.club_name),
                    CLUB_NAME_CHANGE_DATE_LIMIT.toString(),
                    result
            ), false)*/

            clubProfileSetViewModel.checkClubName(binding.cEtLayout.getText())
        }

        binding.cEtLayout.addTextChangedListener(clubNameTextWatcher())
        binding.etClubIntroduce.addTextChangedListener(clubIntroduceTextChangeWatcher())
        binding.etClubIntroduce.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etClubIntroduce.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    private fun saveProfileImage() {
        if (clubProfileSetViewModel.clubProfileImageUriLiveData.value != null) {
            val uri: Uri = clubProfileSetViewModel.clubProfileImageUriLiveData.value!!
            context?.let { it1 ->
                val multipart = uri.asMultipart(
                    "file",
                    "club_${clubId}_${Date().time}_${TextUtils.getRandomString(3)}",
                    it1.contentResolver
                )
                if (multipart != null) {
                    clubProfileSetViewModel.uploadClubProfileImage(
                        getString(R.string.cloudFlareKey),
                        multipart
                    )
                }
            }
        }
    }

    private fun saveClubAllInfo() {
        val requestHashMap = hashMapOf<String, Any>()
        requestHashMap["integUid"] = uid
        requestHashMap["introduction"] = binding.etClubIntroduce.text.toString()
        if (!clubProfileSetViewModel.clubNameCheckLiveData.value?.dataObj?.checkToken.isNullOrEmpty()) {
            requestHashMap["checkToken"] = clubProfileSetViewModel.clubNameCheckLiveData.value?.dataObj?.checkToken!!
            requestHashMap["clubName"] = binding.cEtLayout.getText()
        }
        if (!clubProfileSetViewModel.clubBgImageUploadResultLiveData.value.isNullOrEmpty()) {
            requestHashMap[KEY_CLUB_INFO_BG_IMAGE] = clubProfileSetViewModel.clubBgImageUploadResultLiveData.value!!
        }
        if (!clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value.isNullOrEmpty()) {
            requestHashMap[KEY_CLUB_INFO_PROFILE_IMAGE] = clubProfileSetViewModel.clubProfileImageUploadResultLiveData.value!!
        }
        clubProfileSetViewModel.setClubAllInfo(clubId, requestHashMap)
    }

    private fun clubNameTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p: Editable?) {
            p?.let {
                clubProfileSetViewModel._clubNameCheckLiveData.value = null
                checkingTopbarTailButtonEnable()
                val afterClubName = it.toString()
                var isChanged = false
                if (!afterClubName.isNullOrEmpty()
                    && !clubProfileSetViewModel.currentClubNameLiveData.value.isNullOrEmpty()
                    && !clubProfileSetViewModel.currentClubNameLiveData.value.equals(afterClubName)
                ) {
                    isChanged = true
                }
                binding.cEtLayout.setButtonEnable(isChanged)
            }
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            /*c?.let {
                if(it.length > CLUB_NAME_MAX_LENGTH){
                    showDialog(String.format(getString(R.string.not_correct_length),
                        CLUB_NAME_MIN_LENGTH.toString(), CLUB_NAME_MAX_LENGTH.toString()))
                    return@let
                }
            }*/
            setClubNameWarningText(false, "", false)
            updateClubNameLengthCountText()
        }

    }

    private fun clubIntroduceTextChangeWatcher() = object : TextWatcher {
        override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //Timber.d("beforeTextChanged lenth = ${c!!.length}")
        }

        override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Timber.d("onTextChanged lenth = ${c?.length}")
            c?.let {
                if (it.length > CLUB_INTRODUCE_LENGTH_LIMIT) {
                    try {
                        binding.etClubIntroduce.setText(c.substring(0, CLUB_INTRODUCE_LENGTH_LIMIT))
                        binding.etClubIntroduce.setSelection(CLUB_INTRODUCE_LENGTH_LIMIT)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                context?.let { context ->
                    binding.etClubIntroduce.background =
                        if (it.isNotEmpty()) context.getDrawable(R.drawable.input_field_success) else context.getDrawable(
                            R.drawable.input_field
                        )
                }
                updateClubIntroduceLengthCountText()
            }
        }

        override fun afterTextChanged(p: Editable?) {
            p?.let {
                clubProfileSetViewModel._clubNameCheckLiveData.value = null
                checkingTopbarTailButtonEnable()
            }
        }
    }

    private fun checkingTopbarTailButtonEnable() {
        var isTailButtonEnable = false
        //val clubNameText = binding.cEtLayout.getText()
        val clubIntroduceText = binding.etClubIntroduce.text.toString()
        if ((!clubProfileSetViewModel.clubNameCheckLiveData.value?.dataObj?.checkToken.isNullOrEmpty() && clubProfileSetViewModel.clubNameCheckLiveData.value?.dataObj?.existYn == false)
            ||
            clubAllInfo.introduction != clubIntroduceText ||
            clubProfileSetViewModel.clubProfileImageUriLiveData.value != null ||
            clubProfileSetViewModel.clubBgImageUriLiveData.value != null
        ) {
            isTailButtonEnable = true
        }
        binding.topbar.setTailTextEnable(isTailButtonEnable)
    }

    @SuppressLint("SetTextI18n")
    private fun updateClubNameLengthCountText() {
        if (binding.cEtLayout.getText().isEmpty()) {
            binding.tvClubNameCount.text = binding.cEtLayout.getText().length.toString() +
                    getString(R.string.slash) +
                    CLUB_NAME_MAX_LENGTH
        } else {
            context?.let {
                TextUtils.applyParticalColor(
                    binding.cEtLayout.getText().length.toString(),
                    getString(R.string.slash) +
                            CLUB_NAME_MAX_LENGTH,
                    it.getColor(R.color.primary_default),
                    binding.tvClubNameCount,
                    true
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateClubIntroduceLengthCountText() {
        if (binding.etClubIntroduce.text.toString().isEmpty()) {
            binding.tvClubIntroduceCount.text =
                binding.etClubIntroduce.text.toString().length.toString() +
                        getString(R.string.slash) +
                        CLUB_INTRODUCE_LENGTH_LIMIT
        } else {
            context?.let {
                TextUtils.applyParticalColor(
                    binding.etClubIntroduce.text.toString().length.toString(),
                    getString(R.string.slash) +
                            CLUB_INTRODUCE_LENGTH_LIMIT,
                    it.getColor(R.color.primary_default), binding.tvClubIntroduceCount, true
                )
            }
        }
    }

    private fun setClubNameWarningText(isShow: Boolean, message: String, stateEnable: Boolean) {
        binding.tvClubNameWarning.isEnabled = stateEnable
        binding.tvClubNameWarning.text = message
        binding.tvClubNameWarning.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.tvClubNameDesc.visibility = if (isShow) View.GONE else View.VISIBLE
        if (stateEnable) {
            binding.cEtLayout.setInputState(CommonEditTextLayout.EditorState.SUCCESS)
        }
    }

    private val takePhotoFromAlbumLauncherForClubProfileImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    context?.let {
                        Timber.d(
                            "ClubProfileImage mimeType =  ${
                                ImageUtils.getMimeTypeFromUri(
                                    it,
                                    uri
                                )
                            }"
                        )
                        if (!isAvailableFileTypeFromUri(uri)) {
                            showDialog(getString(R.string.se_a_not_available_file_format))
                            return@registerForActivityResult
                        }
                        if (!isAvailableFileSizeFromUri(getInputStreamFromUri(it, uri))) {
                            showDialog(getString(R.string.se_a_over_file_size))
                            return@registerForActivityResult
                        }
                    }
                    setProfileAvatar(binding.ivProfileThumbnail, uri.toString())
                    clubProfileSetViewModel._clubProfileImageUriLiveData.value = uri
                    checkingTopbarTailButtonEnable()
                } ?: run {

                }
            } else if (result.resultCode != Activity.RESULT_CANCELED) {
                Timber.d("take photo user canceled")
            }
        }


    private val takePhotoFromAlbumLauncherForClubBackgroundImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    context?.let {
                        if (!isAvailableFileTypeFromUri(uri)) {
                            showDialog(getString(R.string.se_a_not_available_file_format))
                            return@registerForActivityResult
                        }
                        if (!isAvailableFileSizeFromUri(getInputStreamFromUri(it, uri))) {
                            showDialog(getString(R.string.se_a_over_file_size))
                            return@registerForActivityResult
                        }
                    }
                    binding.ivDefaultIcon.visibility = View.GONE
                    if (uri.toString().isNotEmpty()) {
                        clubProfileSetViewModel._clubBgImageUriLiveData.value = uri
                        context?.let { context ->
                            Glide.with(context)
                                .load(uri.toString())
                                .into(binding.ivClubBackgroundImage)
                        }
                        checkingTopbarTailButtonEnable()
                    }
                }
            } else if (result.resultCode != Activity.RESULT_CANCELED) {
                Timber.d("take photo user canceled")
            }
        }

    private val takePhotoFromAlbumIntent =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            //action = Intent.ACTION_GET_CONTENT
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png")
            )
        }

    private fun isAvailableFileTypeFromUri(uri: Uri): Boolean {
        context?.let {
            val fileType = it.contentResolver.getType(uri)
            Timber.d("isAvailableFileTypeFromUri fileType = $fileType")
            if (fileType != null) {
                if (fileType.contains("jpg") || fileType.contains("jpeg")
                    || fileType.contains("png")
                ) {
                    return true
                }
            }
        }
        return false
    }

    private fun isAvailableFileSizeFromUri(inputStream: InputStream?): Boolean {
        if (inputStream != null) {
            context?.let {
                val pickPhotoFileSize = inputStream.available()
                if (pickPhotoFileSize in 1..CLUB_PHOTO_MAX_SIZE) {
                    return true
                }
            }
        }
        return false
    }

    private fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                val fileInputStream = context.contentResolver?.openInputStream(uri)
                if (fileInputStream != null) {
                    return fileInputStream
                }
            } catch (e: Exception) {
                Timber.e("take photo, file size check err:" + e.message)
            }
        }
        return null
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}