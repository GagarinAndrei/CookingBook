package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.adapters.recipe.RecipesAdapter
import ru.netology.nerecipe.databinding.FragmentRecipesFeedFavoritesBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavouriteRecipesFeedFragment : Fragment() {

    private val Fragment.packageManager
        get() = activity?.packageManager

    private val viewModel by activityViewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToEditRecipeScreenEvent.observe(this) {
            val direction = FavouriteRecipesFeedFragmentDirections.toEditRecipeFragment()
            findNavController().navigate(direction)
        }

        viewModel.navigateToNewRecipeScreenEvent.observe(this) {
            val direction = FavouriteRecipesFeedFragmentDirections.toNewRecipeFragment()
            findNavController().navigate(direction)
        }

        viewModel.navigateToRecipeScreenEvent.observe(this) { recipe ->
            val direction = FavouriteRecipesFeedFragmentDirections.toRecipeFragment(recipe.id)
            findNavController().navigate(direction)
        }

        viewModel.navigateToAllRecipesScreenEvent.observe(this) {
            val direction = FavouriteRecipesFeedFragmentDirections.toRecipesFeedFragment()
            findNavController().navigate(direction)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecipesFeedFavoritesBinding.inflate(layoutInflater, container, false)
            .also { binding ->

                binding.faveNewRecipeFab.setOnClickListener {
                    viewModel.onAddButtonClicked()
                }

                val recipeAdapter = RecipesAdapter(viewModel)
                binding.faveRecipesRecyclerView.adapter = recipeAdapter

                viewModel.data.observe(viewLifecycleOwner) { recipes ->
                    val favouriteRecipes = recipes.filter { recipe -> recipe.isFavourite }
                    if (favouriteRecipes.isEmpty()) {
                        binding.faveRecipeFeedPlaceholderNotFound.visibility = View.VISIBLE
                    } else {
                        binding.faveRecipeFeedPlaceholderNotFound.visibility = View.GONE
                    }

                    recipeAdapter.submitList(favouriteRecipes)
                }

            }.root
    }
}