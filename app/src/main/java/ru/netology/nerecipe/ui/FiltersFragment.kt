package ru.netology.nerecipe.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapters.filters.FiltersAdapter
import ru.netology.nerecipe.databinding.FragmentFiltersBinding
import ru.netology.nerecipe.dto.Categories
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FiltersFragment : Fragment() {

    private val Fragment.packageManager
        get() = activity?.packageManager

    private val viewModel by activityViewModels<RecipeViewModel>()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFiltersBinding.inflate(layoutInflater, container, false)
            .also { binding ->

                val sharedPref =
                    activity?.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

                viewModel.listOfFilters.observe(viewLifecycleOwner) { listOfFilters ->
                    binding.filtersAllCategoryCheckBox.isChecked = listOfFilters.isEmpty()
                    binding.filtersAllCategoryCheckBox.isClickable = false
                }

                val listOfCategoryNames = mutableListOf<String>()
                for (i in 0 until Categories.values().size) {
                    listOfCategoryNames.add(Categories.values()[i].categoryName)
                }

                val filtersAdapter = FiltersAdapter(viewModel, sharedPref, viewLifecycleOwner)
                binding.filtersRecyclerView.adapter = filtersAdapter

                viewModel.data.observe(viewLifecycleOwner) {
                    filtersAdapter.submitList(listOfCategoryNames)
                }

                binding.applyFilterButton.setOnClickListener {
                    viewModel.onApplyFiltersButtonClicked()
                    findNavController().popBackStack()
                }

                binding.resetFilterButton.setOnClickListener {
                    if (sharedPref != null) {
                        with(sharedPref.edit()) {
                            for (i in 0 until Categories.values().size) {
                                putBoolean(i.toString(), false)
                            }
                            apply()
                        }
                    }
                    viewModel.onResetFiltersButtonClicked()
                    viewModel.filterCheckboxUpdate.value = true
                }

                onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        binding.resetFilterButton.performClick()
                        binding.applyFilterButton.performClick()
                    }
                }
                requireActivity().onBackPressedDispatcher.addCallback(
                    onBackPressedCallback
                )

            }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
    }

    companion object {
        const val SHARED_PREFS_KEY = "ru.netology.nerecipe.PREFERENCE"
    }
}