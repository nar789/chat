package com.rndeep.fns_fantoo.ui.menu.fantooclub.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentSearchBinding
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubFragment.Companion.HANRYU_TIMES_ID
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsClickType
import com.rndeep.fns_fantoo.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Search UI
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var contentsAdapter: ContentsAdapter
    private val args: SearchFragmentArgs by navArgs()

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.setClubId(args.clubId)

        binding = FragmentSearchBinding.inflate(inflater, container, false).apply {
            cancel.setOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            recentSearchAdapter = RecentSearchAdapter { clickType, item ->
                Timber.d("clicked item: $item")
                when (clickType) {
                    RecentSearchClickType.CLICK -> {
                        binding.search.requestFocus()
                        startSearch(item)
                    }
                    RecentSearchClickType.DELETE -> {
                        viewModel.removeSearchString(item)
                    }
                }
            }

            recentSearchList.run {
                adapter = recentSearchAdapter
                val space = resources.getDimensionPixelSize(R.dimen.spacing_eight)
                addItemDecoration(SpaceDecoration(end = space))
            }

            contentsAdapter = ContentsAdapter { contents, clickType ->
                Timber.d("search, clicked contents: $contents, clickType: $clickType")
                when(clickType) {
                    ContentsClickType.ITEM -> {
                        if (viewModel.getClubId() == HANRYU_TIMES_ID) {
                            contents.link?.let { link ->
                                val direction =
                                    link?.let {
                                        SearchFragmentDirections.actionSearchFragmentToHanryuNewsDetailFragment(
                                            it
                                        )
                                    }
                                if (direction != null) {
                                    findNavController().navigate(direction)
                                }
                            }
                        } else {
                            val direction =
                                SearchFragmentDirections.actionSearchFragmentToContentsDetailFragment(
                                    contents
                                )
                            findNavController().navigate(direction)
                        }
                    }
                    ContentsClickType.MORE -> {
                        Timber.d("click more")
                    }
                    ContentsClickType.LIKE -> {
                        Timber.d("click like")
                    }
                    ContentsClickType.DISLIKE -> {
                        Timber.d("click dislike")
                    }
                    ContentsClickType.HONOR -> {
                        Timber.d("click honor")
                    }
                }
            }
            Timber.d("click more")

            searchResultList.run {
                adapter = contentsAdapter
//                setHasFixedSize(true)
                clearDecorations()
                addLibraryItemDecorationDivider()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)

        binding.search.setOnEditorActionListener { textView, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Timber.d("action done text : ${textView.text}")
                    startSearch(textView.text.toString())
                    textView.text = ""
                    dismissKeyboard(textView)
                    true
                }
                else -> false
            }
        }

        binding.inputLayout.setStartIconOnClickListener {
            if(!binding.search.text.isNullOrEmpty()) {
                startSearch(binding.search.text.toString())
                binding.search.setText("")
                dismissKeyboard(binding.search)
            }
        }

        binding.deleteAll.setOnClickListener {
            viewModel.removeAllSearchString()
        }

        viewModel.recentSearch.observe(viewLifecycleOwner) { list ->
            Timber.d("recentSearch list : $list")
            showEmptyMessage(list.isEmpty())
            recentSearchAdapter.submitList(list.toList())
        }

        viewModel.searchResult.observe(viewLifecycleOwner) { list ->
            Timber.d("searchResult list : $list")
            showEmptyResult(list.isEmpty())
            contentsAdapter.submitList(list)
            binding.count.text = list.size.toString()
        }
    }

    private fun showEmptyMessage(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.noSearch.visibility = visibility
    }

    private fun showEmptyResult(isVisible: Boolean) {
        val empty = if (isVisible) View.VISIBLE else View.GONE
        val list = if (isVisible) View.GONE else View.VISIBLE
        Timber.d("showEmptyResult, isVisible: $isVisible, empty: $empty , list: $list")
        binding.recentSearchContainer.visibility = View.GONE
        binding.emptyContainer.visibility = empty
        binding.searchResultContainer.visibility = list
    }

    private fun startSearch(str: String) {
        binding.search.setText(str)
        viewModel.addSearchString(str)
        viewModel.searchFantooClubPosts(str)
    }
}