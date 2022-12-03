package com.rndeep.fns_fantoo.ui.menu.editprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.datepicker.MaterialDatePicker
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.remote.dto.CountryResponse
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.databinding.FragmentEditProfileBinding
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/*
 * Edit Profile
 */
@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var bottomSheet: GenderBottomSheet
    private lateinit var dialogFragment: MenuDialogFragment
    private val viewModel: EditProfileViewModel by viewModels()

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    Timber.d("image : ${result.data}")
                    context?.let {
                        data?.asMultipart(
                            "file",
                            "image_${Date().time}",
                            it.contentResolver
                        )?.let { viewModel.uploadImage(it) }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditProfileBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            val editProfileAdapter = EditProfileAdapter { item ->
                //
                when (item.itemType) {
                    EditProfileItemType.NICKNAME -> {
                        findNavController().navigate(R.id.action_editProfileFragment_to_nicknameFragment)
                    }
                    EditProfileItemType.CONCERN -> {
                        findNavController().navigate(R.id.action_editProfileFragment_to_selectConcernFragment)
                    }
                    EditProfileItemType.COUNTRY -> {
                        showCountryBottomSheet(viewModel.country)
                    }
                    EditProfileItemType.GENDER -> {
                        showGenderBottomSheet(viewModel.myProfile.value!!.gender)
                    }
                    EditProfileItemType.BIRTHDAY -> {
                        showCalendar()
                    }
                    else -> {
                        Timber.d("item : $item")
                    }
                }
            }
            editProfileList.run {
                setHasFixedSize(true)

                adapter = editProfileAdapter
                addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                        AppCompatResources.getDrawable(context, R.drawable.divider_light_gray50)
                            ?.let { setDrawable(it) }
                    }
                )
                isNestedScrollingEnabled = false
                itemAnimator = null
            }

            viewModel.editProfileItem.observe(viewLifecycleOwner) { editProfileItem ->
                editProfileAdapter.submitList(editProfileItem.map { it.copy() })
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setStatusBar(R.color.gray_25, true)

        launchAndRepeatWithViewLifecycle {
            viewModel.state.collect { state ->
                when (state) {
                    is EditProfileUIState.Success -> {
                        Timber.d("state : ${state.codeState}")
                        showMessage(state.codeState)
                    }
                    is EditProfileUIState.Loading -> {
                        Timber.d("loading")
                    }
                    is EditProfileUIState.Error -> {
                        Timber.d("error : ${state.exception}")
                    }
                    else -> Timber.d("else")
                }
            }
        }

        viewModel.myProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                setProfileAvatar(binding.editProfileImg, getImageUrlFromCDN(profile.imageUrl))
                viewModel.updateItems(profile)
            }
        }

        binding.editProfileBadge.setOnClickListener {
            showPhotoPicker()
        }

        binding.editProfileImg.setOnClickListener {
            getImageUrlFromCDN(viewModel.userInfo.userPhoto)?.let {
                Timber.d("user photo: $it")
                val direction =
                    EditProfileFragmentDirections.actionEditProfileFragmentToProfileImageViewerFragment(it)
                findNavController().navigate(direction)
            }
        }
    }

    private fun showPhotoPicker() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }
        startForResult.launch(Intent.createChooser(intent, "Get Album"))
    }

    private fun showGenderBottomSheet(gender: GenderType) {
        bottomSheet = GenderBottomSheet(gender) { genderType ->
            Timber.d("clicked genderType : $genderType")
            updateGender(genderType)
        }
        bottomSheet.show(requireActivity().supportFragmentManager, GenderBottomSheet.TAG)
    }

    private fun updateGender(genderType: GenderType) {
        Timber.d("updateGender")
        viewModel.updateProfileGender(genderType)
        bottomSheet.dismiss()
    }

    private fun updateBirthday(date: String) {
        Timber.d("updateBirthday : $date")
        viewModel.updateProfileBirthday(date)
    }

    private fun showCountryBottomSheet(country: CountryResponse?) {
        Timber.d("showCountryBottomSheet, country: $country")
        val languageBottomSheet = LanguageBottomSheet()
        languageBottomSheet.title = getString(R.string.g_select_country)
        country?.let { country ->
            languageBottomSheet.selectLanguageCode = country.countryCode
        }
        languageBottomSheet.itemClickListener =
            (object : LanguageBottomSheet.ItemClickListener {
                override fun onItemClick(item: Country) {
                    viewModel.updateProfileCountry(item)
                    languageBottomSheet.dismiss()
                }
            })
        languageBottomSheet.show(requireActivity().supportFragmentManager, "country_bottomsheet")
    }

    private fun showCalendar() {

        var birthday = MaterialDatePicker.todayInUtcMilliseconds()
        Timber.d("birthday: $birthday")
        viewModel.userInfo.birthDay?.let {
            birthday = TimeUtils.getEpochMilliTimeFromDate(it)
            Timber.d("birthday: $birthday")
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.s_birthday))
            .setSelection(birthday)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        datePicker.show(requireActivity().supportFragmentManager, "date")
        datePicker.addOnPositiveButtonClickListener { time ->
            val dateTime = TimeUtils.getLocalDateTimeFromEpochMilli(time)
            val updateDate = dateTime.toLocalDate().toString()
            Timber.d("time: $time , dateTime: $dateTime, updateDate: $updateDate")
            updateBirthday(updateDate)
        }
    }

    private fun showMessage(state: EditProfileState) {
        var message = DialogMessage(
            DialogTitle(getString(R.string.n_setting_nickname), null, null),
            DialogButton(getString(R.string.a_yes), getString(R.string.a_no), true),
            isCompleted = false
        )
        dialogFragment = when (state) {
            EditProfileState.EMPTY -> {
                MenuDialogFragment(message) { dismissDialog() }
            }
            EditProfileState.VALID -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_s_use_possible_nickname_use_this), null),
                    DialogButton(getString(R.string.h_confirm), getString(R.string.g_denial), true),
                    isCompleted = false
                )
                MenuDialogFragment(message) { clickType ->
                    when (clickType) {
                        DialogClickType.OK -> {
                            Timber.d("ok")
                        }
                        DialogClickType.CANCEL -> {
                            Timber.d("cancel")
                        }
                    }
                    dismissDialog()
                }
            }
            EditProfileState.DUPLICATED -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.n_setting_nickname), getString(R.string.se_a_already_use_nickname), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = false
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
            EditProfileState.COMPLETED -> {
                message = DialogMessage(
                    DialogTitle(getString(R.string.p_profile_complete), getString(R.string.se_c_congration_profile_complete), null),
                    DialogButton(getString(R.string.h_confirm), null, false),
                    isCompleted = true
                )
                MenuDialogFragment(message) { dismissDialog() }
            }
        }
        Timber.d("state : $state , message : ${message.title}")
        openDialog()
    }

    private fun openDialog() {
        Timber.d("openDialog")
        dialogFragment.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun dismissDialog() {
        Timber.d("dismissDialog")
        dialogFragment.dismiss()
    }

}