package com.rndeep.fns_fantoo.ui.community.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData
import com.rndeep.fns_fantoo.databinding.FragmentCommunitySearchBinding
import com.rndeep.fns_fantoo.ui.club.search.ClubRecentSearchListAdapter
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunitySearchFragment : Fragment() {

    private var _binding: FragmentCommunitySearchBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val searchViewModel: CommunitySearchViewModel by viewModels()

    //검색 리스트 어댑터
    private val recentSearchListAdapter = ClubRecentSearchListAdapter()

    //검색 결과 리스트 어댑터
    private val resultListAdapter = CommunitySearchResultAdapter()

    //itemDeco
    private lateinit var listVerticalItemDeco: VerticalMarginItemDecoration

    //마지막 아이템 부르기
    private var isCallLastItem = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listVerticalItemDeco = VerticalMarginItemDecoration(0f, 4f, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCommunitySearchBinding.inflate(inflater, container, false)
        initView()
        initFunc()
        settingObserver()
        return binding.root
    }

    private fun initView() {
        showHideResultContainer("hide")
        //검색 리스트 recyclerview
        binding.rcRecentSearchList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcRecentSearchList.adapter = recentSearchListAdapter
        //검색 리스트 recyclerview Deco
        binding.rcRecentSearchList.addItemDecoration(
            HorizontalMarginItemDecoration(
                20f,
                4f,
                requireContext()
            )
        )
        //검색 결과
        binding.rcResultList.layoutManager = LinearLayoutManager(context)
        binding.rcResultList.addSingleItemDecoRation(listVerticalItemDeco)
        binding.rcResultList.adapter = resultListAdapter

        //마지막 아이템 확인
        binding.rcResultList.checkLastItemVisible {
            if (it && isCallLastItem &&  resultListAdapter.itemCount>9) {
                isCallLastItem = false
                searchViewModel.changeLoadingState(true)
                searchViewModel.addSearchCommunityPost()
            } else {
//                isCallLastItem=true
            }
        }

        //기존 저장된 검색어 불러오기
        searchViewModel.getCommunitySearchWordList()

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
                //확인 버튼 누를시 검색어 저장
                return if (action == EditorInfo.IME_ACTION_DONE) {
                    if (binding.edtSearchWord.text.toString() != "")
                        searchCommunityPost(binding.edtSearchWord.text.toString())
                    return true
                } else {
                    false
                }
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

        binding.ivSearchIcon.setOnClickListener {
            if (binding.edtSearchWord.text.toString() != "")
                searchCommunityPost(binding.edtSearchWord.text.toString())
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
            showKeyboard(binding.edtSearchWord)
        }

        //검색어 리스트 터치 리스너
        recentSearchListAdapter.setOnDeleteClickListener(object :
            ClubRecentSearchListAdapter.OnWordClickListener {
            override fun onDeleteIconClick(v: View, text: String, position: Int) {
                //삭제 아이콘 선택
                clearWordSearchList(text, position)
            }

            override fun onWordClick(v: View, text: String, position: Int) {
                //단어 선택
                searchCommunityPost(text)
                binding.ivSearchWordClear.visibility = View.VISIBLE
            }
        })

        resultListAdapter.setOnBoardPostClickListener(object :
            BoardListeners.OnBoardPostClickListener {
            override fun onLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {

            }

            override fun onDisLikeClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {

            }

            override fun onHonorClick(
                dbId: Int,
                postItem: PostListData,
                postType: String,
                changePos: Int,
                holder: RecyclerView.ViewHolder
            ) {

            }

            override fun onOptionClick(
                dbId: Int,
                postAuthId: String,
                postType: String,
                changePos: Int,
                postId: Int,
                isPieceBlockYn: Boolean?,
                isUserBlockYN: Boolean?,
                code: String
            ) {

            }

            override fun onPostClick(
                categoryId: String,
                postId: Int,
                postType: String,
                clubId: String?
            ) {
                if(clubId!=null && clubId!="-1"){
                    findNavController().navigate(
                        CommunitySearchFragmentDirections.actionCommunitySearchFragmentToClubPost(
                            postType,categoryId,clubId,postId
                        )
                    )
                }else{
                    findNavController().navigate(
                        CommunitySearchFragmentDirections.actionCommunitySearchFragmentToCommunitypost2(
                            postType, categoryId,postId
                        )
                    )
                }
            }

            override fun onProfileClick(postItem: PostListData) {}
        })

    }

    private fun settingObserver() {
        searchViewModel.searchPostDatas.observe(viewLifecycleOwner) {
            if (it.isEmpty() && resultListAdapter.getItems().isEmpty()) {
                showHideResultContainer("noList")
                return@observe
            } else {
                showHideResultContainer("show")
            }
            val span =
                SpannableStringBuilder(getString(R.string.j_all_with_arg, it.size)).apply {
                    setSpan(
                        ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                        3,
                        3 + it.size.toString().length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            binding.tvResultCount.text = span
            resultListAdapter.setItems(it)
            isCallLastItem = true
        }

        searchViewModel.searchWordListLiveData.observe(viewLifecycleOwner) {
            recentSearchListAdapter.setSearchText(it)
            if (it.size < 1) {
                showHideSearchWordList("hide")
            } else {
                showHideSearchWordList("show")
            }
        }

        //더이상 검색 결과가 없을 시
        searchViewModel.noMoreSearchResult.observe(viewLifecycleOwner) {
            context?.let {
                searchViewModel.changeLoadingState(false)
                it.showCustomSnackBar(binding.root, getString(R.string.se_d_no_more_exist_post))
            }
        }

        searchViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }

    }

    private fun clearWordSearchList(str: String, pos: Int) {
        searchViewModel.deleteWordOfCommunityList(str)
        recentSearchListAdapter.removeItem(str, pos)
        if (recentSearchListAdapter.itemCount < 1) {
            showHideSearchWordList("hide")
        }
    }

    private fun clearAllSearchList() {
        searchViewModel.deleteAllWordOfCommunityList()
        recentSearchListAdapter.clearAllSearchText()
        showHideSearchWordList("hide")
    }

    private fun showHideSearchWordList(type: String) {
        when (type) {
            "hide" -> {
                binding.rcRecentSearchList.visibility = View.GONE
                binding.tvNoRecentSearchWord.visibility = View.VISIBLE
            }
            "show" -> {
                binding.rcRecentSearchList.visibility = View.VISIBLE
                binding.tvNoRecentSearchWord.visibility = View.GONE
            }
        }
    }

    private fun showHideResultContainer(type: String) {
        when (type) {
            "noList" -> {
                binding.rlRecentSearchConatiner.visibility = View.GONE
                binding.rlResultContainer.visibility = View.GONE
                binding.clNoSearchList.visibility = View.VISIBLE
            }
            "show" -> {
                binding.rlRecentSearchConatiner.visibility = View.GONE
                binding.rlResultContainer.visibility = View.VISIBLE
                binding.clNoSearchList.visibility = View.GONE
            }
            "hide" -> {
                binding.rlRecentSearchConatiner.visibility = View.VISIBLE
                binding.rlResultContainer.visibility = View.GONE
                binding.clNoSearchList.visibility = View.GONE
            }
        }
        searchViewModel.changeLoadingState(false)
    }

    private fun searchCommunityPost(searchWord: String) {
        searchViewModel.changeLoadingState(true)
        if (context != null) {
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.edtSearchWord.windowToken, 0)
        }
        binding.edtSearchWord.setText(searchWord)
        binding.edtSearchWord.clearFocus()
        searchViewModel.setInitSearchState()
        searchViewModel.getSearchCommunityPost(searchWord)
        searchViewModel.addCommunitySearchWordList(searchWord)
        binding.rcResultList.scrollToPosition(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}