package com.rndeep.fns_fantoo.ui.club.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubSearchBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClubSearchFragment : BaseFragment<FragmentClubSearchBinding>(FragmentClubSearchBinding::inflate) {

    private val searchViewModel : ClubSearchViewModel by viewModels<ClubSearchViewModel>()

    //최근 검색어 리스트
    private val recentSearchListAdapter= ClubRecentSearchListAdapter()
    //핫 클럽 100
    private val clubSearchHotClubAdapter= ClubSearchListAdapter()
    //검색 결과
    private val clubSearchResultAdapter = ClubSearchListAdapter()
    //자동 저장
    private var isAutoSave=true

    override fun initUi() {
        initSearchView()
        settingObserve()
        settingData()

    }

    override fun initUiActionEvent() {
        binding.tvCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        //자동 저장 기능 변경
        binding.tvCancelAutoSave.setOnClickListener {
            //isAuto => true : 자동저장 활성화 false :자동저장 비활성화 (default : true)
            if(isAutoSave){
                binding.tvCancelAutoSave.text=getString(R.string.j_auto_save_on)
            }else{
                binding.tvCancelAutoSave.text=getString(R.string.j_auto_save_off)
            }
            isAutoSave=!isAutoSave
        }

        // 검색어 저장 리스트 clickListener
        recentSearchListAdapter.setOnDeleteClickListener(object : ClubRecentSearchListAdapter.OnWordClickListener{
            //삭제 버튼 선택시 해당 단어 리스트에 삭제
            override fun onDeleteIconClick(v: View, text: String, position: Int) {
                clearWordSearchList(text,position)
            }
            //단어 클릭시 해당 단어로 검색 실행
            override fun onWordClick(v: View, text: String, position: Int) {
                binding.edtSearchWord.setText(text)
                val inputManager= requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(binding.edtSearchWord.windowToken,0)
                changeSearchList(text)
                binding.edtSearchWord.clearFocus()
            }
        })

        //텍스트 변경시 "" 일 경우 추천 화면 ""이 아닐 경우 검색화면으로 이동
        binding.edtSearchWord.addTextChangedListener({text, start, count, after ->
            //before
        },{text, start, before, count ->
            //text change
        },{text ->
            //after
            text?.let {
                if(text.isEmpty() || text.toString()==""){
                    binding.ivSearchCancel.visibility=View.GONE
                    binding.clResultContainer.visibility=View.GONE
                    binding.rlRecentSearchConatiner.visibility=View.VISIBLE
                }else{
                    binding.ivSearchCancel.visibility=View.VISIBLE
                }
            }
        })

        binding.edtSearchWord.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(textview: TextView?, action: Int, event: KeyEvent?): Boolean {
                var returnValue=false
                //확인 버튼 누를시 검색어 저장 후 검색
                if (action == EditorInfo.IME_ACTION_DONE){
                    doSearch()
                    returnValue=true
                }
                return returnValue
            }

        })

        //검색어 저장 전체 삭제 실행
        binding.tvAllDelete.setOnClickListener {
            clearAllSearchList()
        }

        //추천 클럽 선택
        clubSearchHotClubAdapter.setOnClubClickListener(object : ClubSearchListAdapter.OnClubClickListener{
            override fun onClubClick(v: View, clubId: String) {
                val action = ClubSearchFragmentDirections.actionClubSearchFragmentToClubPageDetail(clubId)
                findNavController().navigate(action)
            }
        })

        //검색 클럽 선택
        clubSearchResultAdapter.setOnClubClickListener(object : ClubSearchListAdapter.OnClubClickListener{
            override fun onClubClick(v: View, clubId: String) {
                val action = ClubSearchFragmentDirections.actionClubSearchFragmentToClubPageDetail(clubId)
                findNavController().navigate(action)
            }
        })

        //edtText 클리어
        binding.ivSearchCancel.setOnClickListener {
            binding.clResultContainer.visibility=View.GONE
            binding.rlRecentSearchConatiner.visibility=View.VISIBLE
            binding.edtSearchWord.setText("")
            showKeyboard(binding.edtSearchWord)
        }

        binding.ivSearchIcon.setOnClickListener {
            doSearch()
        }

    }

    private fun settingObserve(){
        searchViewModel.hotClubItemLiveDate.observe(this){
            binding.tvHotClubText.visibility = (if(it.isNotEmpty())View.VISIBLE else View.INVISIBLE)
            clubSearchHotClubAdapter.submitList(it)
        }

        searchViewModel.clubSearchResultLiveData.observe(this){
            clubSearchResultAdapter.submitList(it){
                if(clubSearchResultAdapter.itemCount==0){
                    binding.llNoSearchClub.visibility=View.VISIBLE
                    binding.resultContainer.visibility=View.GONE
                    binding.tvResultCount.visibility=View.GONE
                }else{
                    binding.llNoSearchClub.visibility=View.GONE
                    binding.resultContainer.visibility=View.VISIBLE
                    binding.tvResultCount.visibility=View.VISIBLE
                    binding.tvResultCount.text=clubSearchResultAdapter.itemCount.toString()
                }
                binding.clResultContainer.visibility=View.VISIBLE
                binding.rlRecentSearchConatiner.visibility=View.GONE
            }
        }

        searchViewModel.searchWordListLiveData.observe(this){
            recentSearchListAdapter.setSearchText(it)
            if( recentSearchListAdapter.itemCount==0){
                binding.rcRecentSearchList.visibility=View.GONE
                binding.tvNoRecentSearch.visibility=View.VISIBLE
            }else{
                binding.rcRecentSearchList.visibility=View.VISIBLE
                binding.tvNoRecentSearch.visibility=View.GONE
            }
        }

    }

    private fun settingData(){
        searchViewModel.getSearchWordList()
        searchViewModel.getHotClubList()
    }

    //검색화면 view 셋팅
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initSearchView(){
        binding.clResultContainer.visibility=View.GONE
        binding.rlRecentSearchConatiner.visibility=View.VISIBLE

        //검색어 리스트 셋팅
        binding.rcRecentSearchList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rcRecentSearchList.addItemDecoration(HorizontalMarginItemDecoration(14f,4f,requireContext()))
        binding.rcRecentSearchList.adapter=recentSearchListAdapter

        //추천 클럽 100 셋팅
        binding.rcHotClub100.layoutManager=LinearLayoutManager(context)
        binding.rcHotClub100.adapter=clubSearchHotClubAdapter

        //검색 결과 셋팅
        binding.rcResultClubList.layoutManager=LinearLayoutManager(context)
        binding.rcResultClubList.adapter=clubSearchResultAdapter

    }

    private fun doSearch(){
        val inputManager= requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.edtSearchWord.windowToken,0)
        changeSearchList(binding.edtSearchWord.text.toString())
        binding.edtSearchWord.clearFocus()
    }

    private fun changeSearchList(searchText :String){
        if(binding.edtSearchWord.text.toString()=="") return
        searchViewModel.searchClub(binding.edtSearchWord.text.toString())
        if(isAutoSave){
            searchViewModel.addSearchWordList(searchText)
        }
    }

    private fun clearWordSearchList(str :String, pos:Int){
        CoroutineScope(Dispatchers.Main).launch {
            searchViewModel.deleteWordOfList(str)
            async {
                recentSearchListAdapter.removeItem(str,pos)
                if( recentSearchListAdapter.itemCount==0){
                    binding.rcRecentSearchList.visibility=View.GONE
                    binding.tvNoRecentSearch.visibility=View.VISIBLE
                }else{
                    binding.rcRecentSearchList.visibility=View.VISIBLE
                    binding.tvNoRecentSearch.visibility=View.GONE
                }
            }

        }

    }

    private fun clearAllSearchList(){
        CoroutineScope(Dispatchers.Main).launch {
            searchViewModel.deleteAllWordOfList()
            async {
                recentSearchListAdapter.clearAllSearchText()
                if( recentSearchListAdapter.itemCount==0){
                    binding.rcRecentSearchList.visibility=View.GONE
                    binding.tvNoRecentSearch.visibility=View.VISIBLE
                }else{
                    binding.rcRecentSearchList.visibility=View.VISIBLE
                    binding.tvNoRecentSearch.visibility=View.GONE
                }
            }.await()
        }
    }

}