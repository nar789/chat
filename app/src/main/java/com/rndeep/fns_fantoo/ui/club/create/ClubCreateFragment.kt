package com.rndeep.fns_fantoo.ui.club.create

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubCreateBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.data.local.model.ClubCreateSendItem
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingItemDecoration
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class ClubCreateFragment :
    BaseFragment<FragmentClubCreateBinding>(FragmentClubCreateBinding::inflate){

    private val clubCreateSettingAdapter: ClubCreateSettingAdapter = ClubCreateSettingAdapter()
    private lateinit var getClubBannerImageResult: ActivityResultLauncher<Intent>
    private lateinit var getClubProfileImageResult: ActivityResultLauncher<Intent>

    private val createViewModel: ClubCreateViewModel by viewModels()

    //셋팅 아이템 리스트
    //승인 방식
    private lateinit var settingAcceptMethodList: ArrayList<BottomSheetItem>

    //클럽 공개 설정
    private lateinit var clubOpenMethodList: ArrayList<BottomSheetItem>

    //주언어 설정
    private lateinit var majorLangList: ArrayList<BottomSheetItem>

    //주 활동 국가 설정
    private lateinit var majorCountryList: ArrayList<BottomSheetItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel.getInitAlertListData(requireContext())
        getClubBannerImageResult =getClubBackGroundActivityResult()
        getClubProfileImageResult =getClubProfileActivityResult()
        setDefaultImage()
    }

    override fun initUi() {
        settingToolbar()
        settingView()
        settingObserver()
    }

    override fun initUiActionEvent() {
        //클럽 설정 리스트들
        clubCreateSettingAdapter.setOnSettingClickListener(object :
            ClubCreateSettingAdapter.OnSettingClickListener {
            override fun onSettingClick(v: View, position: Int, type: Int) {
                showClubSettingBottomSheet(type)
            }
        })

        //클럽 배너 이미지 선택
        binding.flClubThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            getClubBannerImageResult.launch(Intent.createChooser(intent, "GET Album"))

        }

        //profileThumbnail
        binding.clubMyThumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }
            getClubProfileImageResult.launch(Intent.createChooser(intent, "GET Album"))
        }

        binding.edtClubName.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                dismissKeyboard(textView)
                binding.edtClubName.clearFocus()
                true
            } else {
                false
            }
        }

        binding.ivClearText.setOnClickListener {
            clearClubNameEditText()
            initClubNameEditText()
        }

        //클럽 이름 길이측정
        binding.edtClubName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0 ?: return
                initClubNameEditText()
                changeNickNameLengthState(p0.isEmpty(),p0.length)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        //닉네임 중복 확인
        binding.btnCheckDuplicate.setOnClickListener {
            if (!createViewModel.clubNameisEmpty) return@setOnClickListener
            val clubName =binding.edtClubName.text.toString()
            if(clubName.length<2 || clubName.length>30){
                binding.tvClubNameAlert.text= getString(R.string.se_num_write_within_2arg,2,30)
                binding.tvClubNameAlert.setTextColor(requireContext().getColor(R.color.secondary_default))
                binding.tvClubNameAlert.visibility=View.VISIBLE
            }else if(clubName.startsWith(" ")){
                binding.tvClubNameAlert.text= getString(R.string.se_c_cannot_use_blank_first_character)
                binding.tvClubNameAlert.setTextColor(requireContext().getColor(R.color.secondary_default))
                binding.tvClubNameAlert.visibility=View.VISIBLE
            }else{
                createViewModel.clubNameCheck(binding.edtClubName.text.toString())
            }
        }

        //정책 확인
        binding.checkPolicy.setOnCheckedChangeListener { _, isCheck ->
            createViewModel.setChangeClubPolicy(isCheck)
            createViewModel.canGoToNext()
        }

    }

    private fun settingToolbar() {
        //toolbar text 색상 설정
        val s = SpannableString(binding.clubCreateToolbar.menu[0].title)
        s.setSpan(ForegroundColorSpan(requireContext().getColor(R.color.gray_300)), 0, s.length, 0)
        binding.clubCreateToolbar.menu[0].title = s

        binding.clubCreateToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_club_create_next -> {
                    if(createViewModel.canGoNextLiveData.value==true){
                        val clubCreateInfo = ClubCreateSendItem(
                            createViewModel.clubBannerMultipartBody,
                            createViewModel.defaultClubBanner,
                            createViewModel.clubProfileMultipartBody,
                            createViewModel.defaultClubProfile,
                            createViewModel.myClubName!!,
                            createViewModel.clubCheckToken!!,
                            createViewModel.clubOpenJoinMemberYn,
                            createViewModel.clubOpenYn,
                            createViewModel.selectLangCode,
                            createViewModel.selectCountryCode,
                        )
                        val action =ClubCreateFragmentDirections.actionClubCreateFragmentToClubCreateInterestFragment(clubCreateInfo)
                        findNavController().navigate(action)
                    }else{
                        if(createViewModel.clubNameDuplicateCheck.value!=true){
                            showAlert(null,getString(R.string.alert_need_checking_duplicate_nickname))
                        }else if(createViewModel.clubPolicyLiveData.value != true){
                            showAlert(null,getString(R.string.alert_agree_privacy_term))
                        }else{
                            showAlert(null,getString(R.string.error_code_2001))
                        }
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.clubCreateToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun settingView() {
        binding.rcSettingList.layoutManager = LinearLayoutManager(context)
        binding.rcSettingList.adapter = clubCreateSettingAdapter

        binding.rcSettingList.addItemDecoration(
            ClubSettingItemDecoration(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.divider_line
                )
            )
        )

    }

    private fun settingObserver() {
        createViewModel.isClubNameDuplicateAlertLiveData.observe(this) {
            //it => true :닉네임 중복됨 , false : 닉네임 중복 안됨
            if(it){
                binding.tvClubNameAlert.text= getString(R.string.se_a_already_used_club_name)
                binding.tvClubNameAlert.setTextColor(requireContext().getColor(R.color.secondary_default))
            }else{
                binding.tvClubNameAlert.text= getString(R.string.se_s_use_possible_club_name)
                binding.tvClubNameAlert.setTextColor(requireContext().getColor(R.color.primary_default))
            }
            binding.tvClubNameAlert.visibility=View.VISIBLE
        }

        createViewModel.clubSettingItem.observe(this){
            clubCreateSettingAdapter.setSettingItem(it)
            context?.let {
                binding.tvClubCreateDescription.post {
                    if(binding.tvClubCreateDescription.height<SizeUtils.getDpValue(65f,it)){
                        val params = (binding.tvClubCreateDescription.layoutParams as ConstraintLayout.LayoutParams)
                        params.bottomToBottom=-1
                        binding.tvClubCreateDescription.layoutParams=params
                        binding.tvClubCreateDescription.requestLayout()
                    }else{
                        val params = (binding.tvClubCreateDescription.layoutParams as ConstraintLayout.LayoutParams)
                        params.bottomToBottom=binding.clContainer.id
                        binding.tvClubCreateDescription.layoutParams=params
                        binding.tvClubCreateDescription.requestLayout()

                    }
                    try {
                        //"※ " 영역 기준 두번째줄 마진적용 정렬
                        binding.tvClubCreateDescription.setLeftMarinSpan(
                            getString(R.string.se_c_detail_setting_can_in_club_settings).substring(
                                0,
                                2
                            ),
                            getString(R.string.se_c_detail_setting_can_in_club_settings)
                        )
                    }catch (e:Exception){
                        Timber.e("${e.printStackTrace()}")
                    }
                }
            }
        }

        //배너 이미지
        createViewModel.clubBannerImage.observe(this){
            binding.ivClubBannerThumbnail.setImageURI(it)
            binding.ivCameraIcon.visibility = View.GONE
            createViewModel.canGoToNext()
        }
        //썸네일 이미지
        createViewModel.clubThumbnailImage.observe(this){
             val params =
                binding.ivClubProfileImage.layoutParams as ConstraintLayout.LayoutParams
            params.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            binding.ivClubProfileImage.scaleType = ImageView.ScaleType.FIT_XY
            binding.clubMyThumbnail.clipToOutline = true
            binding.ivClubProfileImage.imageTintList=null
            binding.ivClubProfileImage.setImageURI(it)
            binding.ivClubProfileIcon.visibility = View.GONE
            createViewModel.canGoToNext()
        }

        //클럽 정책 확인
        createViewModel.clubPolicyLiveData.observe(this){
            if(it != binding.checkPolicy.isChecked) binding.checkPolicy.isChecked=it
        }

        //닉네임 체크
        createViewModel.clubNameDuplicateCheck.observe(this){
            //true =닉네임 중복체크 완료 / false = 닉네임 중복체크 실패
            changeClubNameState(it)
        }

        createViewModel.acceptMethodListLiveData.observe(this) {
            settingAcceptMethodList = it
            for(a in it){
                if(a.isChecked!!){
                    clubCreateSettingAdapter.getItem()[0].currentSetting=a.itemName
                    clubCreateSettingAdapter.notifyItemChanged(0)
                }
            }
        }

        createViewModel.clubOpenMethodListLiveData.observe(this) {
            clubOpenMethodList = it
            for(a in it){
                if(a.isChecked!!){
                    clubCreateSettingAdapter.getItem()[1].currentSetting=a.itemName
                    clubCreateSettingAdapter.notifyItemChanged(1)
                }
            }
        }

        createViewModel.majorLangListLiveData.observe(this) {
            majorLangList = it
            for(a in it){
                if(a.isChecked!!){
                    clubCreateSettingAdapter.getItem()[2].currentSetting=a.itemName
                    clubCreateSettingAdapter.notifyItemChanged(2)
                }
            }
        }

        createViewModel.majorCountryListLiveData.observe(this) {
            majorCountryList = it
            for(a in it){
                if(a.isChecked!!){
                    clubCreateSettingAdapter.getItem()[3].currentSetting=a.itemName
                    clubCreateSettingAdapter.notifyItemChanged(3)
                }
            }
        }

        createViewModel.canGoNextLiveData.observe(this){
            if (it) {
                val s = SpannableString(binding.clubCreateToolbar.menu[0].title)
                s.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                    0,
                    s.length,
                    0
                )
                binding.clubCreateToolbar.menu[0].title = s
            } else {
                val s = SpannableString(binding.clubCreateToolbar.menu[0].title)
                s.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.gray_300)),
                    0,
                    s.length,
                    0
                )
                binding.clubCreateToolbar.menu[0].title = s
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

    private fun showAlert(alertTitle: String?, alertMessage: String) {
        val dialog = CommonDialog.Builder().apply {
            if (alertTitle != null) title = alertTitle
            message = alertMessage
            positiveButtonString = "확인"
        }
        dialog.build().show(this.parentFragmentManager, "alert")
    }

    private fun changeClubNameState(isCheck: Boolean) {
        //클럽 이름 중복 확인 여부 변경
        if (isCheck) {
            binding.isDuplicateCheckIcon.visibility = View.VISIBLE
            binding.edtClubName.clearFocus()
            binding.edtClubNameContainer.setBackgroundResource(R.drawable.bg_border_radius_8_c_gray50_s_primary500)
        } else {
            binding.isDuplicateCheckIcon.visibility = View.GONE
            binding.edtClubNameContainer.setBackgroundResource(R.drawable.bg_border_radius_8_c_gray50_s_gray100)
        }
        createViewModel.canGoToNext()
    }

    private fun setDefaultImage(){
        createViewModel.defaultClubBanner=requireContext().getString(R.string.club_banner_default)
        val profileDefaultList = resources.getStringArray(R.array.club_profile_default_list).toList()
        createViewModel.defaultClubProfile=profileDefaultList[(Math.random()*5).toInt()]
    }

    private fun clearClubNameEditText(){
        binding.edtClubName.setText("")
        binding.edtClubName.clearFocus()
        dismissKeyboard(binding.edtClubName)
    }

    private fun initClubNameEditText(){
        binding.tvClubNameAlert.visibility=View.GONE
        binding.isDuplicateCheckIcon.visibility = View.GONE
        createViewModel.setClubNameDuplicateCheck(false)
        binding.edtClubNameContainer.setBackgroundResource(R.drawable.bg_border_radius_8_c_gray50_s_gray100)
    }

    private fun changeNickNameLengthState(lengthCheck :Boolean,length :Int){
        val colorSpan : ForegroundColorSpan
        if (lengthCheck) {
            colorSpan=ForegroundColorSpan(requireContext().getColor(R.color.state_enable_gray_200))
            createViewModel.clubNameisEmpty = false
            binding.ivClearText.visibility=View.GONE
        } else {
            colorSpan=ForegroundColorSpan(requireContext().getColor(R.color.primary_default))
            createViewModel.clubNameisEmpty = true
            binding.ivClearText.visibility=View.VISIBLE
        }
        binding.tvClubNameCount.text= SpannableString("${length}/30").apply {
            setSpan(colorSpan,0,"${length}".length,0)
        }
    }

    private fun showClubSettingBottomSheet(type :Int){
        val custom = CustomBottomSheet()
        var alertTitle :String? = ""
        var alertList = arrayListOf<BottomSheetItem>()
        var clickListener : BottomSheetAdapter.OnItemClickListener? =null
        var alertMessage :String? = null
        when (type) {
            ConstVariable.ClubCreateSettingAcceptMethod -> {
                alertTitle=requireContext().getString(R.string.g_join_accept_method)
                alertList=settingAcceptMethodList
                clickListener= object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        createViewModel.clickSettingItem(0,pos,settingAcceptMethodList[pos].itemName)
                        when(settingAcceptMethodList[pos].itemName){
                            context?.getString(R.string.j_auto)->{
                                createViewModel.clubOpenJoinMemberYn=true
                            }
                            context?.getString(R.string.s_approval)->{
                                createViewModel.clubOpenJoinMemberYn=false
                            }
                        }
                        custom.dismiss()
                    }
                }

            }
            ConstVariable.ClubCreateSettingClubOpen -> {
                alertTitle=requireContext().getString(R.string.k_club_visibility_settings)
                alertList=clubOpenMethodList
                clickListener=object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        createViewModel.clickSettingItem(1,pos,clubOpenMethodList[pos].itemName)
                        when(clubOpenMethodList[pos].itemName){
                            context?.getString(R.string.g_open_public)->{
                                createViewModel.clubOpenYn=true
                            }
                            context?.getString(R.string.b_hidden)->{
                                createViewModel.clubOpenYn=false
                            }
                        }
                        custom.dismiss()
                    }
                }
            }
            ConstVariable.ClubCreateSettingMajorLang -> {
                alertTitle=requireContext().getString(R.string.j_main_language_settings)
                alertMessage=requireContext().getString(R.string.se_g_recommend_club_for_language_user)
                alertList=majorLangList
                clickListener=object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        createViewModel.clickSettingItem(2,pos,majorLangList[pos].itemName)
                        //languageCode
                        createViewModel.selectLangCode=majorCountryList[pos].selectLangCode?:"en"
                        custom.dismiss()
                    }
                }
            }
            ConstVariable.ClubCreateSettingMajorCountry -> {
                alertTitle= requireContext().getString(R.string.j_main_activity_contry_settings)
                alertMessage= requireContext().getString(R.string.se_g_recommend_club_for_nation_user)
                alertList= majorCountryList
                clickListener= object : BottomSheetAdapter.OnItemClickListener {
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        createViewModel.clickSettingItem(3,pos,majorCountryList[pos].itemName)
                        createViewModel.selectCountryCode=majorCountryList[pos].selectLangCode?:"US"
                        custom.dismiss()
                    }
                }
            }
        }
        if(alertTitle!=null){
            custom.setTitleText(alertTitle)
        }
        custom.setBottomItems(alertList)
        if(alertMessage!=null){
            custom.setSubTitleText(alertMessage)
        }
        if(clickListener!=null){
            custom.setOnCliCkListener(clickListener)
        }
        activity?.supportFragmentManager?.let { custom.show(it, "") }
    }

    private fun getClubBackGroundActivityResult()=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            if (data == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }
            val fileInfo = getFileRealInfos(data.data)
            if (fileInfo == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }
            val fileName =
                if (fileInfo["FileName"] != null) fileInfo["FileName"].toString() else null
            val fileSize =
                if (fileInfo["FileSize"] != null) fileInfo["FileSize"] as Long else null
            if (fileName == null || fileSize == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }

            if ((fileName.substring(fileName.indexOf(".")) == ".jpg" ||
                        fileName.substring(fileName.indexOf(".")) == ".jpeg" ||
                        fileName.substring(fileName.indexOf(".")) == ".png") &&
                fileSize <= 5000000
            ) {
                Uri.parse(data.dataString).asMultipart(
                    "file",
                    "clubBanner_${Date().time}",
                    requireContext().contentResolver
                ).run {
                    if(this!=null){
                        createViewModel.setClubBannerImage(data.data as Uri,this)
                        binding.ivCameraIcon.visibility = View.GONE
                    }else{
                        showAlert(null, getString(R.string.se_a_not_available_file_format))
                    }
                }
            } else if (fileSize > 5000000) {
                showAlert(null, getString(R.string.se_a_over_file_size))
            } else {
                showAlert(null, getString(R.string.se_a_over_file_size))
            }
        }
    }

    private fun getClubProfileActivityResult()= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            if (data == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }
            val fileInfo = getFileRealInfos(data.data)
            if (fileInfo == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }
            val fileName =
                if (fileInfo["FileName"] != null) fileInfo["FileName"].toString() else null
            val fileSize =
                if (fileInfo["FileSize"] != null) fileInfo["FileSize"] as Long else null
            if (fileName == null || fileSize == null) {
                showAlert(null, getString(R.string.se_a_not_available_file_format))
                return@registerForActivityResult
            }
            if (fileName.substring(fileName.indexOf(".")) == ".jpg" ||
                fileName.substring(fileName.indexOf(".")) == ".jpeg" ||
                fileName.substring(fileName.indexOf(".")) == ".png" ||
                fileSize <= 5000000
            ) {
                Uri.parse(data.data.toString()).asMultipart(
                    "file",
                    "clubProfile_${Date().time}",
                    requireContext().contentResolver
                ).run {
                    if(this!=null){
                        createViewModel.setClubProfileImage(data.data as Uri,this)
                        binding.ivClubProfileIcon.visibility = View.GONE
                    }else{
                        showAlert(null, getString(R.string.se_a_not_available_file_format))
                    }
                }

            } else if (fileSize > 5000000) {
                showAlert(null, getString(R.string.se_a_over_file_size))
            } else {
                showAlert(null, getString(R.string.se_a_over_file_size))
            }
        }
    }

}