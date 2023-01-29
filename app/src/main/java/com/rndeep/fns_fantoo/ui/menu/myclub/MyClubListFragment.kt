package com.rndeep.fns_fantoo.ui.menu.myclub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentMyClubListBinding
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint

/**
 * My Club List UI
 */
@AndroidEntryPoint
class MyClubListFragment : Fragment() {

    private lateinit var binding: FragmentMyClubListBinding
    private lateinit var myClubsAdapter: MyClubsAdapter

    private val viewModel: MyClubsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyClubListBinding.inflate(inflater, container, false)

        myClubsAdapter = MyClubsAdapter { item ->
            val direction = MyClubListFragmentDirections.actionMyClubListFragmentToClubPageDetail(item.clubId)
            findNavController().navigate(direction)
        }
        binding.apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            myClubList.run {
                adapter = myClubsAdapter
            }

            favoriteSwitch.isChecked = viewModel.getFavorite()

            favoriteSwitch.setOnCheckedChangeListener { switch, checked ->
                val textColor: Int = if(checked) {
                    ContextCompat.getColor(switch.context, R.color.primary_default)
                } else {
                    ContextCompat.getColor(switch.context, R.color.gray_300)
                }
                switch.setTextColor(textColor)

                viewModel.setFavorite(checked)
                viewModel.fetchMyClubs(checked)
            }
        }

        viewModel.myClubs.observe(viewLifecycleOwner) { clubs ->
            myClubsAdapter.submitList(clubs)
        }

        viewModel.clubCount.observe(viewLifecycleOwner) { count ->
            if (binding.favoriteSwitch.isChecked) {
                binding.myClubCount.text = requireContext().getString(R.string.favorite_club, count)
            } else {
                binding.myClubCount.text = requireContext().getString(R.string.joined_club, count)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStatusBar(R.color.gray_25, true)
    }

}