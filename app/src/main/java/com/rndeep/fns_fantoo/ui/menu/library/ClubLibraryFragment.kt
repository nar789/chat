package com.rndeep.fns_fantoo.ui.menu.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLibraryClubBinding
import com.rndeep.fns_fantoo.utils.addLibraryItemDecorationDivider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 *  Club Library
 */
@AndroidEntryPoint
class ClubLibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryClubBinding
    private lateinit var libraryPostsAdapter: LibraryClubPostAdapter
    private lateinit var libraryCommentAdapter: LibraryClubCommentAdapter
    private lateinit var librarySaveAdapter: LibraryClubPostAdapter
    private val viewModel: ClubLibraryViewModel by viewModels()

    private var postCount = 0
    private var commentCount = 0
    private var saveCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryClubBinding.inflate(inflater, container, false).apply {
            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                Timber.d("chekedIds : $checkedIds")
                if (checkedIds.size > 0) {
                    setState(checkedIds[0])
                }
            }

            libraryPostsAdapter = LibraryClubPostAdapter { item ->
                Timber.d("clicked item: $item")
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToClubPostDetail(
                        DETAIL_CLUB_POST_TYPE,
                        item.categoryCode,
                        item.postId,
                        item.clubId
                    )
                findNavController().navigate(direction)
            }

            libraryCommentAdapter = LibraryClubCommentAdapter { item ->
                Timber.d("clicked item: $item")
                val replyId = if (item.parentReplyId != 0) {
                    item.parentReplyId
                } else {
                    item.replyId
                }
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToClubComment(
                        true,
                        item.postId,
                        replyId,
                        item.categoryCode,
                        item.clubId
                    )
                findNavController().navigate(direction)
            }

            librarySaveAdapter = LibraryClubPostAdapter { item ->
                Timber.d("clicked item: $item")
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToClubPostDetail(
                        DETAIL_CLUB_POST_TYPE,
                        item.categoryCode,
                        item.postId,
                        item.clubId
                    )
                findNavController().navigate(direction)
            }

            libraryList.run {
                adapter = libraryPostsAdapter
                addLibraryItemDecorationDivider()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.clubMyPosts.observe(viewLifecycleOwner) { list ->
            Timber.d("libraryPosts list: $list , count: ${list.size} ")
            libraryPostsAdapter.submitList(list)
            postCount = list.size
        }

        viewModel.clubMyComments.observe(viewLifecycleOwner) { list ->
            Timber.d("libraryComments list: $list , count: ${list.size}")
            libraryCommentAdapter.submitList(list)
            commentCount = list.size
        }

        viewModel.clubMyBookmarks.observe(viewLifecycleOwner) { list ->
            Timber.d("librarySave list :$list , count: ${list.size}")
            librarySaveAdapter.submitList(list)
            saveCount = list.size
        }
    }

    private fun setState(chipId: Int) {
        var count = 0
        val message = when (chipId) {
            binding.chipWrite.id -> {
                binding.libraryList.adapter = libraryPostsAdapter
                count = postCount
                getString(R.string.se_j_no_write_post)
            }
            binding.chipComment.id -> {
                binding.libraryList.adapter = libraryCommentAdapter
                count = commentCount
                getString(R.string.se_j_no_write_comment)
            }
            binding.chipSave.id -> {
                binding.libraryList.adapter = librarySaveAdapter
                count = saveCount
                getString(R.string.se_j_no_save_post)
            }
            else -> "no message"
        }
        val visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.emptyMsg.text = message
        binding.emptyImg.visibility = visibility
        binding.emptyMsg.visibility = visibility
    }

    companion object {
        const val DETAIL_CLUB_POST_TYPE = "club"
    }
}