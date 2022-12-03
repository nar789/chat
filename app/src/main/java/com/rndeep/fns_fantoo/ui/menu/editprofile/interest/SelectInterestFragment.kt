package com.rndeep.fns_fantoo.ui.menu.editprofile.interest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentSelectInterestBinding
import com.rndeep.fns_fantoo.utils.InterestSpaceDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Select Interest UI
 */
@AndroidEntryPoint
class SelectInterestFragment : Fragment() {

    private lateinit var binding: FragmentSelectInterestBinding
    private lateinit var interestAdapter: SelectInterestAdapter
    private val viewModel: SelectInterestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        interestAdapter = SelectInterestAdapter { item ->
            Timber.d("click item : $item")
            viewModel.updateCheckedItem(item)
            updateSaveBtn(viewModel.checkEnableCount())
        }

        binding = FragmentSelectInterestBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            interestList.run {
                adapter = interestAdapter
                itemAnimator = null
                addItemDecoration(InterestSpaceDecoration())
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.interestItems.observe(viewLifecycleOwner) { list ->
            Timber.d("list: $list")
            interestAdapter.submitList(list.map { it.copy() })
        }

        binding.saveBtn.setOnClickListener {
            viewModel.updateInterest()
        }
    }

    private fun updateSaveBtn(enable: Boolean) {
        Timber.d("updateSaveBtn")
        binding.saveBtn.isEnabled = enable
        if(enable) {
            binding.saveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.state_active_primary_default))
        } else {
            binding.saveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.state_disabled_gray_200))
        }
    }

}