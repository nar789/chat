package com.rndeep.fns_fantoo.ui.menu.fantooclub.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.databinding.FragmentCategoryBinding
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubFragment
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubFragmentDirections
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubPagerAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.ContentsClickType
import com.rndeep.fns_fantoo.utils.addItemDecorationWithoutFirstDivider
import com.rndeep.fns_fantoo.utils.clearDecorations
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Channel UI
 */
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var contentsAdapter: ContentsAdapter
    private lateinit var selectedCategoryAdapter: SelectedCategoryAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(FantooClubPagerAdapter.KEY_CLUB_ID)?.let { viewModel.setClubId(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCategoryBinding.inflate(inflater, container, false).apply {

            categoryAdapter = CategoryAdapter { category ->
                Timber.d("category: $category")
                selectedCategoryAdapter = SelectedCategoryAdapter(category.categoryName) {
                    categoryList.run {
                        adapter = categoryAdapter
                        setHasFixedSize(true)
                        setBackgroundColor(requireContext().getColor(R.color.bg_light_gray_50))
                        clearDecorations()
                    }
                }
                contentsAdapter = ContentsAdapter { contents, clickType ->
                    Timber.d("clicked contents: $contents, clickType: $clickType")
                    when(clickType) {
                        ContentsClickType.ITEM -> {
                            Timber.d("click item")
                            navigateToDetail(contents)
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
                concatAdapter = ConcatAdapter(selectedCategoryAdapter, contentsAdapter)
                categoryList.run {
                    adapter = concatAdapter
                    setHasFixedSize(true)
                    addItemDecorationWithoutFirstDivider()
                    setBackgroundColor(requireContext().getColor(R.color.gray_25))
                }
                lifecycleScope.launch {
                    viewModel.fetchFantooClubPosts(viewModel.getClubId(), category.categoryCode).collectLatest {
                        contentsAdapter.submitData(it)
                    }
                }
            }
            categoryList.run {
                adapter = categoryAdapter
                setHasFixedSize(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchFantooClubCategories(viewModel.getClubId())

        viewModel.categories.observe(viewLifecycleOwner) { list ->
            Timber.d("list: $list")
            categoryAdapter.submitList(list)
        }

    }

    private fun navigateToDetail(contents: FantooClubPost) {
        when (viewModel.getClubId()) {
            FantooClubFragment.FANTOO_TV_ID -> {
                val direction =
                    FantooClubFragmentDirections.actionFantooClubFragmentToContentsDetailFragment(
                        contents
                    )
                findNavController().navigate(direction)
            }
            FantooClubFragment.HANRYU_TIMES_ID -> {
                contents.link.let { link ->
                    val direction =
                        link?.let {
                            FantooClubFragmentDirections.actionFantooClubFragmentToHanryuNewsDetailFragment(
                                it
                            )
                        }
                    if (direction != null) {
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }
}