package ru.netology.nerecipe.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapters.steps.StepsAdapter
import ru.netology.nerecipe.databinding.FragmentRecipeEditBinding
import ru.netology.nerecipe.dto.Categories
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipeViewModel
import ru.netology.nerecipe.utils.hideKeyboard


class EditRecipeFragment : Fragment() {
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
                viewModel.editStepImg.value = uri.toString()
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
                viewModel.editRecipeImg.value = uri.toString()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRecipeEditBinding.inflate(layoutInflater, container, false).also { binding ->
            with(binding) {

                val currentEditingRecipe = MutableLiveData<Recipe>()

                currentEditingRecipe.value = viewModel.editingRecipe.value

                currentEditingRecipe.observe(viewLifecycleOwner) { recipe ->
                    render(recipe)
                }

                editRecipeNameEditText.setText(currentEditingRecipe.value?.title)

                val categories = currentEditingRecipe.value?.tags ?: mutableSetOf()

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
                                    currentEditingRecipe.value =
                                        currentEditingRecipe.value?.copy(tags = categories)
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

                var recipeImgPath: String

                editRecipeAddRecipeImageMaterialButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
                    pickRecipeImgActivityResultLauncher.launch(imgPickIntent)
                }

                viewModel.editRecipeImg.observe(viewLifecycleOwner) { path ->
                    recipeImgPath = path
                    currentEditingRecipe.value =
                        currentEditingRecipe.value?.copy(recipeImgPath = recipeImgPath)
                }

                var steps = currentEditingRecipe.value?.steps ?: mutableMapOf()
                var stepImgPath = ""

                editRecipeAddImageStepImageButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "image/*"
                    }
                    val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
                    pickStepImgActivityResultLauncher.launch(imgPickIntent)
                }
                viewModel.editStepImg.observe(viewLifecycleOwner) { path ->
                    stepImgPath = path
                }

                editRecipeAddStepImageButton.setOnClickListener {
                    if (!editRecipeStepsEditText.text.isNullOrEmpty()) {
                        steps[editRecipeStepsEditText.text.toString()] = stepImgPath.ifEmpty { "" }
                        currentEditingRecipe.value =
                            currentEditingRecipe.value?.copy(steps = steps)
                        stepImgPath = ""
                        editRecipeStepsEditText.hideKeyboard()
                        editRecipeStepsEditText.text.clear()
                    }
                }

                viewModel.editingRecipe.observe(viewLifecycleOwner) { recipe ->
                    steps = recipe.steps
                    currentEditingRecipe.value = currentEditingRecipe.value?.copy(steps = steps)
                }

                editRecipeSaveButton.setOnClickListener {
                    if (
                        !editRecipeNameEditText.text.isNullOrBlank() &&
                        steps.isNotEmpty()
                    ) {
                        currentEditingRecipe.value = currentEditingRecipe.value?.copy(
                            title = editRecipeNameEditText.text.toString()
                        )
                        currentEditingRecipe.value?.let { recipe ->
                            viewModel.onEditRecipeSaveButtonClicked(
                                recipe
                            )
                            findNavController().popBackStack()
                        }
                    } else if (editRecipeNameEditText.text.isNullOrBlank()) {
                        editRecipeNameEditText.requestFocus()
                        editRecipeNameEditText.error =
                            resources.getString(R.string.error_empty_name)
                    } else if (steps.isEmpty()) {
                        editRecipeStepsEditText.requestFocus()
                        editRecipeStepsEditText.error =
                            resources.getString(R.string.error_empty_steps)
                    }
                }
            }

        }.root
    }


    private fun FragmentRecipeEditBinding.render(recipe: Recipe) {

        editRecipeImageView.setImageURI(Uri.parse(recipe.recipeImgPath))

        setTags(editRecipeChipGroup.context, recipe.tags, this)

        val stepsAdapter = StepsAdapter(recipe, CALLER_EDIT_RECIPE, viewModel)
        editRecipeStepsList.adapter = stepsAdapter
        stepsAdapter.submitList(recipe.steps.keys.toList())
    }

    companion object {
        const val CALLER_EDIT_RECIPE = "Caller: editRecipe"
    }
}

private fun setTags(
    context: Context,
    categories: MutableSet<Categories>,
    binding: FragmentRecipeEditBinding
) {
    val chipGroup = binding.editRecipeChipGroup
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