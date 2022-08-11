package ru.gagarin.cookingbook.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import ru.gagarin.cookingbook.R
import ru.gagarin.cookingbook.adapters.steps.StepsAdapter
import ru.gagarin.cookingbook.databinding.FragmentRecipeNewBinding
import ru.gagarin.cookingbook.dto.Categories
import ru.gagarin.cookingbook.dto.Recipe
import ru.gagarin.cookingbook.viewModel.RecipeViewModel
import ru.gagarin.cookingbook.utils.hideKeyboard


class NewRecipeFragment : Fragment() {

    private val Fragment.packageManager
        get() = activity?.packageManager

    private val viewModel by activityViewModels<RecipeViewModel>()

    private val pickStepImgActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.newStepImg.value = uri.toString()
            }
        }

    private val pickRecipeImgActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.newRecipeImg.value = uri.toString()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecipeNewBinding.inflate(layoutInflater, container, false).also { binding ->
            with(binding) {

                val currentNewRecipe = MutableLiveData(
                    Recipe(
                        id = 0,
                        title = "",
                        recipeImgPath = "",
                        steps = mutableMapOf(),
                        tags = mutableSetOf()
                    )
                )

                currentNewRecipe.observe(viewLifecycleOwner) { recipe ->
                    render(recipe)
                }

                val categories = mutableSetOf<Categories>()

                val popupMenu by lazy {
                    PopupMenu(context, binding.addCategoryImageButton).apply {
                        inflate(R.menu.recipe_categories_menu)
                        for (i in 0 until Categories.values().size) {
                            menu.add(0, i, 0, Categories.values()[i].categoryName)
                        }
                        setOnMenuItemClickListener { option ->
                            for (i in 0 until Categories.values().size) {
                                if (option.itemId == i) {
                                    categories.add(Categories.values()[i])
                                    currentNewRecipe.value =
                                        currentNewRecipe.value?.copy(tags = categories)
                                    return@setOnMenuItemClickListener true
                                }
                            }
                            false
                        }
                    }
                }

                addCategoryImageButton.setOnClickListener {
                    popupMenu.show()
                }

                var recipeImgPath = ""

                viewModel.newRecipeImg.observe(viewLifecycleOwner) { path ->
                    recipeImgPath = path
                    currentNewRecipe.value =
                        currentNewRecipe.value?.copy(recipeImgPath = recipeImgPath)
                }

                newRecipeAddRecipeImageMaterialButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
                    pickRecipeImgActivityResultLauncher.launch(imgPickIntent)
                }

                var steps = mutableMapOf<String, String>()
                var stepImgPath = ""

                newRecipeAddImageStepImageButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
                    pickStepImgActivityResultLauncher.launch(imgPickIntent)
                }

                viewModel.newStepImg.observe(viewLifecycleOwner) { path ->
                    stepImgPath = path
                }

                newRecipeAddStepImageButton.setOnClickListener {
                    if (!newRecipeStepsEditText.text.isNullOrEmpty()) {
                        steps[newRecipeStepsEditText.text.toString()] = stepImgPath.ifEmpty { "" }
                        currentNewRecipe.value =
                            currentNewRecipe.value?.copy(steps = steps)
                        stepImgPath = ""
                        newRecipeStepsEditText.hideKeyboard()
                        newRecipeStepsEditText.text.clear()
                    }
                }

                viewModel.newRecipe.observe(viewLifecycleOwner) { recipe ->
                    steps = recipe.steps
                    currentNewRecipe.value = currentNewRecipe.value?.copy(steps = recipe.steps)
                }

                newRecipeSaveButton.setOnClickListener {
                    if (recipeImgPath.isEmpty()) {
                        recipeImgPath = DEFAULT_IMAGE
                        currentNewRecipe.value =
                            currentNewRecipe.value?.copy(recipeImgPath = recipeImgPath)
                    }
                    if (
                        !newRecipeNameEditText.text.isNullOrBlank() &&
                        steps.isNotEmpty()
                    ) {
                        currentNewRecipe.value = currentNewRecipe.value?.copy(
                            title = newRecipeNameEditText.text.toString()
                        )
                        currentNewRecipe.value?.let { recipe ->
                            viewModel.onCreateRecipeSaveButtonClicked(
                                recipe
                            )
                            findNavController().popBackStack()
                        }
                    } else if (newRecipeNameEditText.text.isNullOrBlank()) {
                        newRecipeNameEditText.requestFocus()
                        newRecipeNameEditText.error =
                            resources.getString(R.string.error_empty_name)
                    } else if (steps.isEmpty()) {
                        newRecipeStepsEditText.requestFocus()
                        newRecipeStepsEditText.error =
                            resources.getString(R.string.error_empty_steps)
                    }
                }
            }

        }.root
    }

    private fun FragmentRecipeNewBinding.render(recipe: Recipe) {

        newRecipeImageView.setImageURI(Uri.parse(recipe.recipeImgPath))

        setTags(newRecipeChipGroup.context, recipe.tags, this)

        val stepsAdapter = StepsAdapter(recipe, CALLER_NEW_RECIPE, viewModel)
        newRecipeStepsList.adapter = stepsAdapter
        stepsAdapter.submitList(recipe.steps.keys.toList())
    }


    companion object {
        private const val DEFAULT_IMAGE =
            "android.resource://ru.gagarin.cookingbook/drawable/placeholder_main"
        const val CALLER_NEW_RECIPE = "Caller: newRecipe"
    }
}

private fun setTags(
    context: Context,
    categories: MutableSet<Categories>,
    binding: FragmentRecipeNewBinding
) {
    val chipGroup = binding.newRecipeChipGroup

    chipGroup.removeAllViews()

    categories.forEach { category ->
        val tagName = category.categoryName
        val chip = Chip(context)
        chip.text = tagName
        chip.isCloseIconVisible = true

        chip.setOnCloseIconClickListener {
            categories.remove(category)
            chipGroup.removeView(chip)
        }
        chipGroup.addView(chip)
    }
}




