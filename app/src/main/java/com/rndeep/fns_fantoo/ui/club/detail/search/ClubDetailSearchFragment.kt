package com.rndeep.fns_fantoo.ui.club.detail.search

import android.content.Context
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubDeatilSearchBinding
import com.rndeep.fns_fantoo.ui.club.search.ClubRecentSearchListAdapter
import com.rndeep.fns_fantoo.ui.community.search.CommunitySearchResultAdapter
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.VerticalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@AndroidEntryPoint
class ClubDetailSearchFragment : Fragment() {

    private var _binding: FragmentClubDeatilSearchBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val searchViewModel: ClubDetailSearchViewModel by viewModels()

    private val getArg : ClubDetailSearchFragmentArgs by navArgs()
    //검색 리스트 어댑터
    private val recentSearchListAdapter = ClubRecentSearchListAdapter()

    //검색 결과 리스트 어댑터
    private val resultListAdapter = ClubSearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.setDarkStatusBarIcon()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentClubDeatilSearchBinding.inflate(inflater, container, false)
        initView()
        initFunc()
        settingObserver()
        return binding.root
    }

    private fun initView() {
        showHideResultContainer("hide")
        //검색 리스트 recyclerview
        binding.rcRecentSearchList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcRecentSearchList.adapter = recentSearchListAdapter
        //검색 리스트 recyclerview Deco
        binding.rcRecentSearchList.addItemDecoration(
            HorizontalMarginItemDecoration(
                14f,
                4f,
                requireContext()
            )
        )

        //검색 결과
        binding.rcResultList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcResultList.addItemDecoration(VerticalMarginItemDecoration(0f,4f,requireContext()))
        binding.rcResultList.adapter = resultListAdapter

        //기존 검색어 저장
        searchViewModel.getClubDetailSearchWordList()

        recentSearchListAdapter.setOnDeleteClickListener(object :
            ClubRecentSearchListAdapter.OnWordClickListener {
            override fun onDeleteIconClick(v: View, text: String, position: Int) {
                //삭제 아이콘 선택
                clearWordSearchList(text, position)
            }

            override fun onWordClick(v: View, text: String, position: Int) {
                //단어 선택
                searchViewModel.getSearchClubPost(getArg.clubId,text)
                searchViewModel.addClubDetailSearchWord(text)
                binding.edtSearchWord.setText(text)
                binding.ivSearchWordClear.visibility = View.VISIBLE
            }
        })

        //포커싱
        binding.edtSearchWord.requestFocus()
        binding.edtSearchWord.postDelayed({
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(binding.edtSearchWord, InputMethodManager.SHOW_FORCED)
        }, 30)

    }

    private fun initFunc() {
        //edit Text 단어 입력
        binding.edtSearchWord.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                textview: TextView?,
                action: Int,
                event: KeyEvent?
            ): Boolean {
                var returnValue = false
                //확인 버튼 누를시 검색어 저장
                if (action == EditorInfo.IME_ACTION_DONE) {
                    context?.let {
                        val inputManager =
                            it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.hideSoftInputFromWindow(binding.edtSearchWord.windowToken, 0)
                        changeSearchList()
                        binding.edtSearchWord.clearFocus()
                        searchViewModel.getSearchClubPost(getArg.clubId,binding.edtSearchWord.text.toString())
                        returnValue = true
                    }
                }
                return returnValue
            }
        })
        //text watcher
        binding.edtSearchWord.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    binding.ivSearchWordClear.visibility = View.VISIBLE
                } else {
                    binding.ivSearchWordClear.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //취소 버튼 선택
        binding.tvCancel.setOnClickListener {
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.edtSearchWord.windowToken, 0)
            findNavController().popBackStack()
        }

        //전제 삭제
        binding.tvAllDelete.setOnClickListener {
            clearAllSearchList()
        }

        //검색중인 글자 지우기
        binding.ivSearchWordClear.setOnClickListener {
            binding.edtSearchWord.setText("")
            binding.edtSearchWord.clearFocus()
            showHideResultContainer("hide")
        }

    }

    private fun settingObserver() {
        searchViewModel.searchPostDatas.observe(viewLifecycleOwner) {
            showHideResultContainer("show")
            val span = SpannableStringBuilder(getString(R.string.j_all_with_arg, it.size)).apply {
                setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                    3,
                    3 + it.size.toString().length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.tvResultCount.text=span
            resultListAdapter.setItems(it)
        }

        searchViewModel.searchWordListLiveData.observe(viewLifecycleOwner){
            recentSearchListAdapter.setSearchText(it)
            if (it.size < 1) {
                binding.rcRecentSearchList.visibility = View.GONE
                binding.tvNoRecentSearchWord.visibility = View.VISIBLE
            } else {
                binding.rcRecentSearchList.visibility = View.VISIBLE
                binding.tvNoRecentSearchWord.visibility = View.GONE
            }
        }
    }

    private fun changeSearchList() {
        if (binding.edtSearchWord.text.toString() == "") return
        searchViewModel.addClubDetailSearchWord(binding.edtSearchWord.text.toString())
        context?.let {
            binding.tvAllDelete.setTextColor(it.getColor(R.color.gray_800))
        }
    }

    private fun clearWordSearchList(str: String, pos: Int) {
        searchViewModel.deleteWordOfClubDetailWordList(str)
        recentSearchListAdapter.removeItem(str, pos)
        if(recentSearchListAdapter.itemCount<1){
            binding.rcRecentSearchList.visibility = View.GONE
            binding.tvNoRecentSearchWord.visibility = View.VISIBLE
            context?.let {
                binding.tvAllDelete.setTextColor(it.getColor(R.color.gray_400))
            }
        }
    }

    private fun clearAllSearchList() {
        searchViewModel.deleteAllWordOfClubDetailWordList()
        recentSearchListAdapter.clearAllSearchText()
        binding.rcRecentSearchList.visibility = View.GONE
        binding.tvNoRecentSearchWord.visibility = View.VISIBLE
        context?.let {
            binding.tvAllDelete.setTextColor(it.getColor(R.color.gray_400))
        }
    }

    private fun showHideResultContainer(type: String) {
        when (type) {
            "show" -> {
                binding.rlRecentSearchConatiner.visibility = View.GONE
                binding.rlResultContainer.visibility = View.VISIBLE
            }
            "hide" -> {
                binding.rlRecentSearchConatiner.visibility = View.VISIBLE
                binding.rlResultContainer.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(param1: String, param2: String) = ClubDetailSearchFragment()
    }
}