package ru.gagarin.cookingbook.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import ru.gagarin.cookingbook.R
import ru.gagarin.cookingbook.adapters.recipe.RecipesAdapter
import ru.gagarin.cookingbook.databinding.FragmentRecipesFeedBinding
import ru.gagarin.cookingbook.viewModel.RecipeViewModel

class RecipesFeedFragment : Fragment() {

    private val Fragment.packageManager
        get() = activity?.packageManager

    private val viewModel by activityViewModels<RecipeViewModel>()

    private val openSearch = MutableLiveData(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToEditRecipeScreenEvent.observe(this) {
            val direction =
                RecipesFeedFragmentDirections.toEditRecipeFragment()
            findNavController().navigate(direction)
        }

        viewModel.navigateToNewRecipeScreenEvent.observe(this) {
            val direction = RecipesFeedFragmentDirections.toNewRecipeFragment()
            findNavController().navigate(direction)
        }

        viewModel.navigateToRecipeScreenEvent.observe(this) { recipe ->
            val direction = RecipesFeedFragmentDirections.toRecipeFragment(recipe.id)
            findNavController().navigate(direction)
        }

        viewModel.navigateToFaveRecipesScreenEvent.observe(this) {
            val direction =
                RecipesFeedFragmentDirections.toFaveRecipesFeedFragment()
            findNavController().navigate(direction)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search_button -> {
                if (openSearch.value != null) {
                    openSearch.value = !openSearch.value!!
                }
                true
            }

            R.id.menu_filter_button -> {
                val direction = RecipesFeedFragmentDirections.toFiltersFragment()
                findNavController().navigate(direction)
                true
            }
            else -> false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecipesFeedBinding.inflate(layoutInflater, container, false)
            .also { binding ->

                setHasOptionsMenu(true)

                binding.newRecipeFab.setOnClickListener {
                    viewModel.onAddButtonClicked()
                }

                val recipeAdapter = RecipesAdapter(viewModel)
                binding.recipesRecyclerView.adapter = recipeAdapter

                viewModel.data.observe(viewLifecycleOwner) { recipes ->
                    if (recipes.isNullOrEmpty()) {
                        binding.recipeFeedPlaceholderMain.visibility = View.VISIBLE
                    } else {
                        binding.recipeFeedPlaceholderMain.visibility = View.GONE
                    }

                    recipeAdapter.setData(recipes.toMutableList())
                }

                viewModel.filterRecipes.observe(viewLifecycleOwner) {
                    if (viewModel.filteredRecipes.value.isNullOrEmpty()) {
                        binding.recipeFeedPlaceholderMain.visibility = View.VISIBLE
                    } else {
                        binding.recipeFeedPlaceholderMain.visibility = View.GONE
                    }

                    recipeAdapter.setData(viewModel.filteredRecipes.value?.toMutableList())
                }

                openSearch.observe(viewLifecycleOwner) { openSearch ->
                    if (openSearch) {
                        binding.recipesSearchView.visibility = View.VISIBLE
                        binding.newRecipeFab.visibility = View.GONE
                    } else {
                        binding.recipesSearchView.visibility = View.GONE
                        binding.newRecipeFab.visibility = View.VISIBLE
                    }

                    binding.recipesSearchView.setOnQueryTextListener(object :
                        SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String): Boolean {
                            if (recipeAdapter.filter(newText)) {
                                binding.recipeFeedPlaceholderMain.visibility = View.VISIBLE
                            } else {
                                binding.recipeFeedPlaceholderMain.visibility = View.GONE
                            }
                            return true
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {

                            return false
                        }
                    }
                    )
                }

            }.root
    }

    companion object {
        const val CALLER_FEED = "Caller: feed"
    }
}