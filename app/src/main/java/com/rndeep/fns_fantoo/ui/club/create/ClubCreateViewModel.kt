package com.rndeep.fns_fantoo.ui.club.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.data.remote.model.toCountryEntity
import com.rndeep.fns_fantoo.data.remote.model.toLanguageEntity
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.data.local.model.SettingItem
import com.rndeep.fns_fantoo.ui.common.LanguageRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ClubCreateViewModel @Inject constructor(
    private val createRepository: ClubCreateRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {

    var accessToken :String? =null
    var uId :String? =null

    init {
        viewModelScope.launch {
            accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
            uId = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
        }
    }

    var defaultClubBanner :String?=null
    var defaultClubProfile :String?=null

    //클럽 배너 이미지
    var clubBannerMultipartBody :MultipartBody.Part? =null
    private val _clubBannerImage =MutableLiveData<Uri>()
    val clubBannerImage :LiveData<Uri> get() = _clubBannerImage
    fun setClubBannerImage(bannerImage :Uri,bannerMultipartBody: MultipartBody.Part){
        clubBannerMultipartBody=bannerMultipartBody
        defaultClubBanner=null
        _clubBannerImage.value=bannerImage
    }

    //클럽 썸네일 이미지
    var clubProfileMultipartBody :MultipartBody.Part? =null
    private var _clubThumbnailImage =MutableLiveData<Uri>()
    val clubThumbnailImage :LiveData<Uri> get() = _clubThumbnailImage
    fun setClubProfileImage(thumbnailImage :Uri,profileMultipart :MultipartBody.Part){
        clubProfileMultipartBody=profileMultipart
        defaultClubProfile=null
        _clubThumbnailImage.value=thumbnailImage
    }
    //클럽 이름 길이 확인
    var clubNameisEmpty = false
    //클럽 중복 확인
    private val _clubNameDuplicateCheck = MutableLiveData<Boolean>(false)
    val clubNameDuplicateCheck :LiveData<Boolean> get() = _clubNameDuplicateCheck
    fun setClubNameDuplicateCheck(isCheck : Boolean){
        _clubNameDuplicateCheck.value=isCheck
    }
    //클럽 썸네일 확인
//    var clubBannerThumbnail = false
    //클럽 프로필 확인
//    var clubProfileThumbnail = false

    private val _clubPolicyLiveData = MutableLiveData<Boolean>()
    val clubPolicyLiveData :LiveData<Boolean> get() = _clubPolicyLiveData
    fun setChangeClubPolicy(isPolicy :Boolean){
        _clubPolicyLiveData.value = isPolicy
    }

    var clubOpenJoinMemberYn =true
    var clubOpenYn=true
    var selectLangCode="en"
    var selectCountryCode="EN"

    private val _canGoNextLiveData = MutableLiveData<Boolean>()
    val canGoNextLiveData : LiveData<Boolean> get() = _canGoNextLiveData
    fun canGoToNext(){
        _canGoNextLiveData.value=_clubNameDuplicateCheck.value==true
                && _clubPolicyLiveData.value==true
                && clubCheckToken!=null
                && myClubName!=null
//        _canGoNextLiveData.value=clubNameLengthCheck&&clubNameDuplicateCheck.value==false&&clubBannerThumbnail&&clubProfileThumbnail&&clubPolicyLiveData.value==true
    }

    //클럽 이름 중복 확인
    var myClubName :String? =null
    var clubCheckToken : String? =null
    private val _isClubNameDuplicateAlertLiveData = SingleLiveEvent<Boolean>()
    val isClubNameDuplicateAlertLiveData: SingleLiveEvent<Boolean> get() = _isClubNameDuplicateAlertLiveData
    fun clubNameCheck(clubName: String) {
        viewModelScope.launch {
            createRepository.checkDuplicateClubName(clubName).run {
                if(this.second!=null){
                    _isClubNameDuplicateAlertLiveData.value=true
                    _clubNameDuplicateCheck.value=false
                }else{
                    val checkResultItem =this.first!!
                    if(!checkResultItem.existYn){
                        myClubName=clubName
                        clubCheckToken=checkResultItem.checkToken
                    }
                    _isClubNameDuplicateAlertLiveData.value=checkResultItem.existYn
                    _clubNameDuplicateCheck.value=!checkResultItem.existYn
                }
            }

        }
    }

    //클럽 셋팅의 현재 상태 리스트 아이템
    private val _clubSettingItem = MutableLiveData<ArrayList<SettingItem>>()
    val clubSettingItem: LiveData<ArrayList<SettingItem>> get() = _clubSettingItem

    //승인 방식
    private val _acceptMetHodListLiveData = MutableLiveData<ArrayList<BottomSheetItem>>()
    val acceptMethodListLiveData: LiveData<ArrayList<BottomSheetItem>> get() = _acceptMetHodListLiveData

    //클럽 공개 설정
    private val _clubOpenMethodListLiveData = MutableLiveData<ArrayList<BottomSheetItem>>()
    val clubOpenMethodListLiveData: LiveData<ArrayList<BottomSheetItem>> get() = _clubOpenMethodListLiveData

    //주 언어 설정
    private val _majorLangListLiveData = MutableLiveData<ArrayList<BottomSheetItem>>()
    val majorLangListLiveData: LiveData<ArrayList<BottomSheetItem>> get() = _majorLangListLiveData

    //주 활동 국가 설정
    private val _majorCountryListLiveData = MutableLiveData<ArrayList<BottomSheetItem>>()
    val majorCountryListLiveData: LiveData<ArrayList<BottomSheetItem>> get() = _majorCountryListLiveData

    //초기 데이터 값 설정
    fun getInitAlertListData(context: Context) {
        viewModelScope.launch {
            //초기 셋팅 선택값
            if(_clubSettingItem.value==null)
                _clubSettingItem.value = arrayListOf(
                    SettingItem(
                        ConstVariable.ClubCreateSettingAcceptMethod,
                        context.getString(R.string.g_join_accept_method),
                        context.getString(R.string.j_auto)
                    ),
                    SettingItem(
                        ConstVariable.ClubCreateSettingClubOpen,
                        context.getString(R.string.k_club_visibility_settings),
                        context.getString(R.string.g_open_public)
                    ),
                    SettingItem(
                        ConstVariable.ClubCreateSettingMajorLang,
                        context.getString(R.string.j_main_language_settings),
                        ""
                    ),
                    SettingItem(
                        ConstVariable.ClubCreateSettingMajorCountry,
                        context.getString(R.string.j_main_activity_contry_settings),
                        ""
                    ),
                )

            //초기 승인방식
            if(_acceptMetHodListLiveData.value==null)
                _acceptMetHodListLiveData.value = arrayListOf(
                    BottomSheetItem(
                        null,
                        context.getString(R.string.j_auto),
                        context.getString(R.string.g_join_immediately_after_apply),
                        true
                    ),
                    BottomSheetItem(
                        null,
                        context.getString(R.string.s_approval),
                        context.getString(R.string.k_join_after_approves),
                        false
                    ),
                )

            //초기 클럽 공개 설정
            if(_clubOpenMethodListLiveData.value==null)
                _clubOpenMethodListLiveData.value = arrayListOf(
                    BottomSheetItem(
                        null,
                        context.getString(R.string.g_open_public),
                        context.getString(R.string.g_post_open_if_open_visiblity),
                        true
                    ),
                    BottomSheetItem(
                        null,
                        context.getString(R.string.b_hidden),
                        context.getString(R.string.k_hide_all_post_in_club),
                        false
                    ),
                )

            //주 나라 설정
            settingCountryList(context)

            settingLanguageList()
        }
    }

    //초기 셋팅 값 설정
//    private fun settingInitSettingValue(context: Context){
//        viewModelScope.launch {
//            if(_clubSettingItem.value==null)
//                _clubSettingItem.value = arrayListOf(
//                    SettingItem(
//                        ConstVariable.ClubCreateSettingAcceptMethod,
//                        context.getString(R.string.club_create_setting_regist_title),
//                        context.getString(R.string.club_create_setting_regist_method1)
//                    ),
//                    SettingItem(
//                        ConstVariable.ClubCreateSettingClubOpen,
//                        context.getString(R.string.club_create_setting_open_title),
//                        context.getString(R.string.club_create_setting_open_method1)
//                    ),
//                    SettingItem(
//                        ConstVariable.ClubCreateSettingMajorLang,
//                        context.getString(R.string.club_create_setting_major_lang_title),
//                        dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"한국어"
//                    ),
//                    SettingItem(
//                        ConstVariable.ClubCreateSettingMajorCountry,
//                        context.getString(R.string.club_create_setting_major_country_title),
//                        dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)?:"Republic of Korea"
//                    ),
//                )
//        }
//    }

    //주 언어 설정
    private fun settingLanguageList(){
        viewModelScope.launch {
            val arrayLanguageList = arrayListOf<BottomSheetItem>()
            val currentLangCode= dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE)
            selectLangCode=currentLangCode?:"en"
            languageRepository.getLanguageAllDB().run {
                if(this.isEmpty()){
                    languageRepository.getSupportLanguageAll().run {
                        when(this){
                            is ResultWrapper.Success->{
                                val langItem = this.data
                                languageRepository.addAllLanguageToDB(langItem.stream().map(Language::toLanguageEntity).collect(
                                    Collectors.toList()))
                                langItem.forEach { language ->
                                    arrayLanguageList.add(
                                        BottomSheetItem(null,
                                            language.name,
                                            isChecked = language.langCode==currentLangCode,
                                            selectLangCode = language.langCode
                                        )
                                    )
                                }
                            }
                            is ResultWrapper.GenericError->{

                            }
                            is ResultWrapper.NetworkError->{

                            }
                        }
                    }
                }else{
                    this.forEach { language ->
                        arrayLanguageList.add(
                            BottomSheetItem(null,
                                language.name,
                                isChecked = language.langCode==currentLangCode,
                                selectLangCode = language.langCode
                            )
                        )
                    }
                }
            }
            //주 언어 설정
            if(_majorLangListLiveData.value==null)
                _majorLangListLiveData.value = arrayLanguageList


        }
    }

    //주 활동 국가 설정
    private fun settingCountryList(context:Context){
        viewModelScope.launch {
            val arrayLanguageList = arrayListOf<BottomSheetItem>()
            val locale = context.resources.configuration.locales.get(0)
            val deviceCode = locale.country
            selectCountryCode=deviceCode
            languageRepository.getCountryAllDB().run {
                if(this.isEmpty()){
                    languageRepository.getCountryAll().run {
                        when(this){
                            is ResultWrapper.Success->{
                                val country = this.data
                                languageRepository.addAllCountryDB(country.stream().map(Country::toCountryEntity).collect(
                                    Collectors.toList()))
                                country.forEach { countryItem ->
                                    arrayLanguageList.add(
                                        BottomSheetItem(null,countryItem.nameEn,
                                            isChecked = (countryItem.iso2==deviceCode||countryItem.iso3==deviceCode),
                                            selectLangCode= countryItem.iso2 )
                                    )
                                }
                            }
                            is ResultWrapper.GenericError->{

                            }
                            is ResultWrapper.NetworkError->{

                            }
                        }
                    }
                }else{
                    this.forEach { country ->
                        arrayLanguageList.add(
                            BottomSheetItem(null,country.nameEn,
                                isChecked = (country.iso2==deviceCode||country.iso3==deviceCode),
                                selectLangCode=country.iso2)
                        )
                    }
                }
            }
            //주 언어 설정
            if(_majorCountryListLiveData.value==null)
                _majorCountryListLiveData.value = arrayLanguageList


        }

    }

    //아이템 선택시
    fun clickSettingItem(listPos :Int,bottomSheetPos:Int, changeSettingValue : String) {
//        _clubSettingItem.value=_clubSettingItem.value?.apply {
//            this[listPos].currentSetting=changeSettingValue
//        }
        when(listPos){
            ConstVariable.ClubCreateSettingAcceptMethod ->{
                _acceptMetHodListLiveData.value=_acceptMetHodListLiveData.value?.apply {
                    for((index,a) in this.withIndex() ){
                        if(index==bottomSheetPos)  this[bottomSheetPos].isChecked=true
                        else this[index].isChecked=false
                    }
                }
            }
            ConstVariable.ClubCreateSettingClubOpen->{
                _clubOpenMethodListLiveData.value=_clubOpenMethodListLiveData.value?.apply {
                    for((index,a) in this.withIndex() ){
                        if(index==bottomSheetPos)  this[bottomSheetPos].isChecked=true
                        else this[index].isChecked=false
                    }
                }
            }
            ConstVariable.ClubCreateSettingMajorLang->{
                _majorLangListLiveData.value=_majorLangListLiveData.value?.apply {
                    for((index,a) in this.withIndex() ){
                        if(index==bottomSheetPos)  this[bottomSheetPos].isChecked=true
                        else this[index].isChecked=false
                    }
                }
            }
            ConstVariable.ClubCreateSettingMajorCountry->{
                _majorCountryListLiveData.value=_majorCountryListLiveData.value?.apply {
                    for((index,a) in this.withIndex() ){
                        if(index==bottomSheetPos)  this[bottomSheetPos].isChecked=true
                        else this[index].isChecked=false
                    }
                }
            }
        }
    }

}