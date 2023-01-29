package com.rndeep.fns_fantoo.ui.club.settings.management

import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.LeadingMarginSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItemPacerable
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingFreeboardOpenwordSetBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.data.TagItem
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.FreeBoardTagViewAdapter
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubFreeBoardOpenWordViewModel
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.AddResult
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_FREEBOARD_OPEN_WORD_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.LanguageUtils
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


val ps = "[\\d\\s\\p{L}]".toRegex()

@AndroidEntryPoint
class ClubFreeBoardOpenWordSetFragment :
    ClubSettingBaseFragment<FragmentClubSettingFreeboardOpenwordSetBinding>(
        FragmentClubSettingFreeboardOpenwordSetBinding::inflate
    ) {

    private val clubFreeBoardOpenWordViewModel: ClubFreeBoardOpenWordViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var clubCategoryItemPacerable: ClubCategoryItemPacerable
    private var snackbar: Snackbar? = null

    override fun initUi() {
        val args: ClubFreeBoardOpenWordSetFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        clubCategoryItemPacerable = args.clubCategoryItem!!

        clubFreeBoardOpenWordViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubFreeBoardOpenWordViewModel.freeBoardOpenWordList.observe(viewLifecycleOwner) { tagItemList ->
            (binding.rvWords.adapter as FreeBoardTagViewAdapter).submitList(tagItemList)
        }
        lifecycleScope.launch {
            val categorySortedList = clubCategoryItemPacerable.categoryList.toSortedSet(
                compareBy({ !it.commonYn },
                    { it.sort })
            )
            //Timber.d("categorySortedList $categorySortedList")

            val tagItemList = mutableListOf<TagItem>()
            for (category in categorySortedList) {
                tagItemList.add(
                    TagItem(
                        category.commonYn,
                        category.categoryName!!
                    )
                )
            }
            clubFreeBoardOpenWordViewModel._freeBoardOpenWordList.value = tagItemList
        }

        setTopSaveButtonEnable(false)
        val tagViewAdapter = FreeBoardTagViewAdapter { selectedItem: TagItem ->
            listItemClicked(selectedItem)
        }
        binding.rvWords.adapter = tagViewAdapter
        binding.rvWords.layoutManager = object : FlexboxLayoutManager(context) {
            override fun getFlexDirection(): Int {
                return FlexDirection.ROW
            }

            override fun getFlexWrap(): Int {
                return FlexWrap.WRAP
            }

            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        setLeftMarinSpan(binding.tvGuide1, getString(R.string.se_s_summury_guide1))
        setLeftMarinSpan(binding.tvGuide2, getString(R.string.se_g_summury_guide2))
        setLeftMarinSpan(binding.tvGuide3, getString(R.string.se_g_summury_guide3))
        setLeftMarinSpan(binding.tvGuide4, getString(R.string.se_m_summury_guide4))
        setLeftMarinSpan(binding.tvGuide5, getString(R.string.se_m_summury_guide5))

        //복붙 block
        binding.etWord.isLongClickable = false
        binding.etWord.setTextIsSelectable(false)
        binding.etWord.customInsertionActionModeCallback = actionModeCallBack
        binding.etWord.customSelectionActionModeCallback = actionModeCallBack

        lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etWord.requestFocus()
            showSoftInput(binding.etWord)
        }

        clubFreeBoardOpenWordViewModel.openWordSaveResultLiveData.observe(viewLifecycleOwner) { clubCategoryItemResponse ->
            when (clubCategoryItemResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    lifecycleScope.launch {
                        context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                        delay(500)
                        navController.popBackStack()
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, clubCategoryItemResponse.code)
                }
            }
        }

        clubFreeBoardOpenWordViewModel.getOpenWordList()
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.topbar.setTailTextClickListener {
            //말머리 저장
            val openWordList =
                clubFreeBoardOpenWordViewModel._freeBoardOpenWordList.value?.filter { !it.isFixedItem }
            if (openWordList != null) {
                clubFreeBoardOpenWordViewModel.saveOpenWordList(
                    clubId, clubCategoryItemPacerable.categoryCode,
                    hashMapOf(
                        KEY_UID to uid,
                        "categoryNameList" to openWordList.map { it.name }
                    )
                )
            }
        }

        /*binding.etWord.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                for (index in start until end) {
                    *//*
                    val type: Int = Character.getType(source?.get(index) ?: ' ')
                    if (type == Character.SURROGATE.toInt() || type == Character.NON_SPACING_MARK.toInt() || type == Character.OTHER_SYMBOL.toInt()
                    ) {return ""
                   }*//*
                    val bitChar = source?.get(index)
                    if (ps.find(bitChar.toString()) == null) {
                        //Timber.d("match $source")
                        return ""
                    }
                }
                return source
            }
        })*/

        binding.etWord.addTextChangedListener(object : TextWatcher {
            var beforeText: String = ""
            var afterPos: Int = -1
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.let {
                    beforeText = it.toString()
                    afterPos = after
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    try {
                        val tempBeforeText = beforeText
                        if (start == 0 && afterPos == 0) {
                            return
                        }
                        if (tempBeforeText == it) return

                        val changedChar: Char = if (start != 0) {
                            it[start]
                        } else {
                            it[afterPos - 1]
                        }

                        Timber.d("onTextChanged char :$changedChar# , $it")
                        var hasSpaceChar = false
                        for (char in it) {//이모지입력 + 영문 입력후 스페이스 입력시 체크
                            if (char == ' ') {
                                hasSpaceChar = true
                                break
                            }
                        }

                        if (changedChar == ' ' || hasSpaceChar) {
                            if (tempBeforeText.isEmpty()) {
                                showSnackBar(getString(R.string.se_m_write_summury))
                                binding.etWord.setText("")
                                return
                            } else {
                                //특수문자, 이모지 검사
                                for (bitChar in tempBeforeText.iterator()) {
                                    if (ps.find(bitChar.toString()) == null) {
                                        binding.etWord.setText(tempBeforeText)
                                        binding.etWord.setSelection(tempBeforeText.length)
                                        showSnackBar(getString(R.string.se_t_not_allow_words))
                                        return
                                    }
                                }

                                val addResult = clubFreeBoardOpenWordViewModel.addOpenWord(
                                    TagItem(
                                        false,
                                        tempBeforeText
                                    )
                                )
                                when (addResult) {
                                    AddResult.AddResultMaxItem -> {//10개 등록된 상태
                                        binding.etWord.setText(tempBeforeText)
                                        showSnackBar(getString(R.string.se_m_headline_count))
                                        return
                                    }
                                    AddResult.AddResultDuplicateItem -> {//동일 문구있음
                                        try {
                                            binding.etWord.setText(tempBeforeText)
                                            binding.etWord.setSelection(tempBeforeText.length)
                                        } catch (e: Exception) {
                                            Timber.e("AddResultDuplicateItem openWord err, ${e.message}")
                                        }
                                        showSnackBar(getString(R.string.se_a_already_used_summury))
                                        return
                                    }
                                    AddResult.AddResultOk -> {
                                        setTopSaveButtonEnable(true)
                                        binding.etWord.setText("")
                                        beforeText = ""
                                    }
                                }
                            }
                        } else {
                            if (it.length > CLUB_FREEBOARD_OPEN_WORD_MAX_LENGTH) {
                                try {
                                    binding.etWord.setText(tempBeforeText)
                                    binding.etWord.text?.let { it1 ->
                                        binding.etWord.setSelection(
                                            it1.lastIndex + 1
                                        )
                                    }
                                } catch (e: Exception) {
                                    Timber.e("clubFreeBoard word max length err, ${e.message}")
                                }
                                showSnackBar(
                                    getString(
                                        R.string.se_j_write_min_max_limit_length,
                                        1.toString(),
                                        CLUB_FREEBOARD_OPEN_WORD_MAX_LENGTH.toString()
                                    )
                                )
                                return
                            }
                        }
                    } catch (ioobe: IndexOutOfBoundsException) {
                        Timber.e("onTextChanged ioobe")
                    }
                }
            }
        })
    }

    private fun listItemClicked(item: TagItem) {
        if (item.isFixedItem) {
            showSnackBar(getString(R.string.se_g_cannot_delete_default_summury))
        } else {
            showDialog(
                "",
                getString(R.string.se_d_ask_deleting_summury),
                "",
                getString(R.string.a_yes),
                getString(R.string.a_no),
                object : CommonDialog.ClickListener {
                    override fun onClick() {
                        clubFreeBoardOpenWordViewModel.removeOpenWord(item)
                        setTopSaveButtonEnable(true)
                    }
                },
                null
            )
        }
    }

    private fun setTopSaveButtonEnable(isEnable: Boolean) {
        binding.topbar.setTailTextEnable(isEnable)
    }

    private fun setLeftMarinSpan(textView: TextView, text: String) {
        val frontCharTextWidth = textView.paint.measureText("· ").toInt()
        textView.text = SpannableString(text).apply {
            setSpan(
                LeadingMarginSpan.Standard(0, frontCharTextWidth),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //setSpan(BulletSpan(), 0, 0 , 0)//optional
        }
    }

    private val actionModeCallBack = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
        }
    }

    private fun showSnackBar(message: String) {
        context?.let { context ->
            if (snackbar == null || snackbar?.isShown == false) {
                snackbar = context.showCustomSnackBar(
                    binding.root, message
                )
            }
        }
    }

}