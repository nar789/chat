package com.rndeep.fns_fantoo.ui.menu.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLibraryBinding
import com.rndeep.fns_fantoo.ui.menu.library.LibraryPagerAdapter.Companion.CLUB_PAGE_INDEX
import com.rndeep.fns_fantoo.ui.menu.library.LibraryPagerAdapter.Companion.COMMUNITY_PAGE_INDEX
import com.rndeep.fns_fantoo.utils.setStatusBar

/**
 *  Menu - Library UI
 */
class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false).apply {
            back.setOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = LibraryPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.gray_25, true)
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            COMMUNITY_PAGE_INDEX -> getString(R.string.k_community)
            CLUB_PAGE_INDEX -> getString(R.string.k_club)
            else -> null
        }
    }
}