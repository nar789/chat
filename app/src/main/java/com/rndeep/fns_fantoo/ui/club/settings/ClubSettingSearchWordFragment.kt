package com.rndeep.fns_fantoo.ui.club.settings

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingSearchWordBinding
import com.rndeep.fns_fantoo.ui.club.settings.adapter.TagViewAdapter
import com.rndeep.fns_fantoo.ui.club.settings.data.TagItem
import com.rndeep.fns_fantoo.ui.club.settings.management.ps
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.AddResult
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubSearchWordViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_SEARCH_WORD_MAX_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_SEARCH_WORD_MIN_LENGTH
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingSearchWordFragment
    : ClubSettingBaseFragment<FragmentClubSettingSearchWordBinding>(
    FragmentClubSettingSearchWordBinding::inflate
) {
    private val clubSearchWordViewModel: ClubSearchWordViewModel by viewModels()
    private lateinit var clubId:String
    private lateinit var uid:String
    private var snackbar: Snackbar? = null
    override fun initUi() {
        val args : ClubSettingSearchWordFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        clubSearchWordViewModel.loadingStatusLiveData.observe(viewLifecycleOwner){
            loadingState ->
            setLoadingView(loadingState)
        }

        clubSearchWordViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        //해쉬리스트 tagItem으로 어댑터 셋
        clubSearchWordViewModel.clubHashTagList.observe(viewLifecycleOwner){
                clubHashTagList ->
            (binding.rvClubSearchWord.adapter as TagViewAdapter).submitList(clubHashTagList)
        }

        //해쉬리스트 겟
        clubSearchWordViewModel.clubHashTagListLiveData.observe(viewLifecycleOwner){
            clubHashTagListResponse ->
            Timber.d("clubHashTagListResponse $clubHashTagListResponse")
            if(clubHashTagListResponse.code == ConstVariable.RESULT_SUCCESS_CODE){
                val clubHashTagListData = clubHashTagListResponse.dataObj
                if(clubHashTagListData != null){
                    val tagItemList = mutableListOf<TagItem>()
                    for(tagString:String in clubHashTagListData.hashtagList){
                        tagItemList.add(TagItem(false, tagString))
                    }
                    clubSearchWordViewModel._clubHashTagList.value = tagItemList
                }
            }
        }

        clubSearchWordViewModel.clubHashTagListSaveResultLiveData.observe(viewLifecycleOwner){
            clubHashTagListSaveResultResponse ->
            if(clubHashTagListSaveResultResponse.code == ConstVariable.RESULT_SUCCESS_CODE){
                context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                lifecycleScope.launch {
                    delay(500)
                    navController.popBackStack()
                }
            }else{
                requireContext().showErrorSnackBar(binding.root, clubHashTagListSaveResultResponse.code)
            }
        }
        val tagViewAdapter = TagViewAdapter() { selectListItem: TagItem ->
            listItemClicked(selectListItem)
        }
        binding.rvClubSearchWord.adapter = tagViewAdapter
        binding.rvClubSearchWord.layoutManager = object : FlexboxLayoutManager(context) {
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
        setTopTailTextEnable(false)

        binding.etClubSearchWord.addTextChangedListener(textWacher)

        //복붙 block
        binding.etClubSearchWord.isLongClickable = false
        binding.etClubSearchWord.setTextIsSelectable(false)
        binding.etClubSearchWord.customInsertionActionModeCallback = actionModeCallBack
        binding.etClubSearchWord.customSelectionActionModeCallback = actionModeCallBack

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(300)
            binding.etClubSearchWord.requestFocus()
            showSoftInput(binding.etClubSearchWord)
        }

        clubSearchWordViewModel.getSearchWordList(clubId, uid)
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
        binding.topbar.setTailTextClickListener {
            //저장
            val tagList = clubSearchWordViewModel.clubHashTagList.value!!.map { it.name }
            val requestHashMap = hashMapOf("hashtagList" to tagList,
            KEY_UID to uid)
            clubSearchWordViewModel.saveSearchWordList(clubId, requestHashMap)
        }

        /*binding.etClubSearchWord.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                for (index in start until end) {
                    val bitChar = source?.get(index)
                    if (ps.find(bitChar.toString()) == null) {
                        //Timber.d("match $source")
                        return ""
                    }
                }
                return source
            }

        }, InputFilter.LengthFilter(15))*/
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

    private val textWacher = object : TextWatcher {
        var beforeText: String = ""
        var afterPos: Int = -1
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            s?.let {
                beforeText = s.toString()
                Timber.d("beforeTextChanged $beforeText")
                afterPos = after
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

        @SuppressLint("StringFormatMatches")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                try {
                    val tempBeforeText = beforeText //상단검색어 선택시 체크중 beforeTextChanged가 전달됨
                    if (start == 0 && afterPos == 0) {
                        return
                    }
                    if (tempBeforeText == it) return

                    val changedChar: Char = if (start != 0) {
                        it[start]
                    } else {
                        it[afterPos - 1]
                    }
                    Timber.d("changedChar : $changedChar")
                    Timber.d(
                        "onTextChanged start: $start, before = $before, afterPos = $afterPos, count = $count, char =$it, " +
                                "beforeText = $tempBeforeText"
                    )
                    var hasSpaceChar = false
                    for (char in it) {//이모지입력 + 영문 입력후 스페이스 입력시 체크
                        if (char == ' ') {
                            hasSpaceChar = true
                            break
                        }
                    }

                    if (changedChar == ' ' || hasSpaceChar) {
                        //스페이스만 입력
                        if (tempBeforeText.isEmpty()) {
                            binding.etClubSearchWord.setText("")
                            showSnackBar(getString(R.string.se_g_write_search_word))
                            return
                        } else {
                            //한글자 입력
                            if (tempBeforeText.length == 1) {
                                binding.etClubSearchWord.setText(tempBeforeText)
                                binding.etClubSearchWord.setSelection(1)
                                showSnackBar(getString(
                                    R.string.se_j_write_min_max_limit_length,
                                    CLUB_SEARCH_WORD_MIN_LENGTH,
                                    CLUB_SEARCH_WORD_MAX_LENGTH
                                ))
                                return
                            }
                            //특수문자, 이모지 검사
                            for (bitChar in tempBeforeText.iterator()) {
                                if (ps.find(bitChar.toString()) == null) {
                                    binding.etClubSearchWord.setText(tempBeforeText)
                                    binding.etClubSearchWord.setSelection(tempBeforeText.length)
                                    showSnackBar(getString(R.string.se_t_not_allow_words))
                                    return
                                }
                            }
                            //val adapter = binding.rvClubSearchWord.adapter as TagViewAdapter
                            val addResult = clubSearchWordViewModel.addSearchWord(TagItem(
                                false,
                                tempBeforeText
                            ))

                            when (addResult) {
                                AddResult.AddResultMaxItem -> {//10개 등록된 상태
                                    binding.etClubSearchWord.setText(tempBeforeText)
                                    showSnackBar(getString(R.string.se_k_club_keyword_count))
                                    return
                                }
                                AddResult.AddResultDuplicateItem -> {//동일 문구있음
                                    try {
                                        binding.etClubSearchWord.setText(tempBeforeText)
                                        binding.etClubSearchWord.setSelection(tempBeforeText.length)
                                    } catch (e: Exception) {
                                        Timber.e("AddResultDuplicateItem err, ${e.message}")
                                    }
                                    showSnackBar(getString(R.string.se_a_already_used_word))
                                    return
                                }
                                AddResult.AddResultOk -> {
                                    setTopTailTextEnable(true)
                                    binding.etClubSearchWord.setText("")
                                    beforeText = ""
                                }
                            }
                        }
                    } else {
                        if (it.length > CLUB_SEARCH_WORD_MAX_LENGTH) {
                            try {
                                binding.etClubSearchWord.setText(tempBeforeText)
                                binding.etClubSearchWord.text?.let { it1 ->
                                    binding.etClubSearchWord.setSelection(
                                        it1.lastIndex + 1
                                    )
                                }
                            } catch (e: Exception) {
                                Timber.e("clubSearch word max length err, ${e.message}")
                            }
                            showSnackBar(getString(
                                R.string.se_j_write_min_max_limit_length,
                                CLUB_SEARCH_WORD_MIN_LENGTH.toString(),
                                CLUB_SEARCH_WORD_MAX_LENGTH.toString()
                            ))
                            return
                        }
                    }
                } catch (ioobe: IndexOutOfBoundsException) {
                    Timber.e("onTextChanged ioobe")
                }
            }
        }
    }

    private fun listItemClicked(tagItem: TagItem) {
        setTopTailTextEnable(true)
        clubSearchWordViewModel.removeSearchWord(tagItem)
    }

    private fun setTopTailTextEnable(isEnable: Boolean) {
        binding.topbar.setTailTextEnable(isEnable)
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

    private fun setLoadingView(isShow:Boolean){
        binding.loadingView.visibility = if(isShow)View.VISIBLE else View.GONE
    }

}

