package ru.gagarin.cookingbook.adapters.recipe

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import ru.gagarin.cookingbook.R
import ru.gagarin.cookingbook.databinding.RecipeCardBinding
import ru.gagarin.cookingbook.dto.Categories
import ru.gagarin.cookingbook.dto.Recipe
import java.util.*

class RecipesAdapter(

    private val interactionListener: RecipeInteractionListener

) : ListAdapter<Recipe, RecipesAdapter.RecipesViewHolder>(DiffCallback) {

    private var list = mutableListOf<Recipe>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(inflater, parent, false)
        return RecipesViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(list: MutableList<Recipe>?) {
        this.list = list!!
        submitList(list)
    }

    class RecipesViewHolder(
        private val binding: RecipeCardBinding,
        private val interactionListener: RecipeInteractionListener

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.recipeCardOptionsMaterialButton).apply {
                inflate(R.menu.recipe_options_menu)
                setOnMenuItemClickListener { option ->
                    when (option.itemId) {
                        R.id.deleteRecipeOption -> {
                            interactionListener.onDeleteMenuOptionClicked(recipe.id)
                            true
                        }
                        R.id.editRecipeOption -> {
                            interactionListener.onEditMenuOptionClicked(recipe.id)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
        }

        init {
            binding.faveRecipeCardMaterialButton.setOnClickListener {
                interactionListener.onFavouriteButtonClicked(
                    recipe
                )
            }
            binding.recipeCardOptionsMaterialButton.setOnClickListener {
                popupMenu.show()
            }

            binding.root.setOnClickListener {
                interactionListener.onRecipeClicked(
                    recipe
                )
            }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe
            with(binding) {
                titleRecipeCardTextView.text = recipe.title
                recipeCardImageView.setImageURI(Uri.parse(recipe.recipeImgPath))
                faveRecipeCardMaterialButton.isChecked = recipe.isFavourite
                setTags(chipGroup.context, recipe.tags, binding)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return newItem == oldItem
        }

    }

    fun filter(query: CharSequence?): Boolean {
        val filteredList = mutableListOf<Recipe>()
        if (query == null || query.isEmpty()) {
            filteredList.addAll(list)
        } else {
            for (item in list) {
                if (
                    item.title
                        .lowercase(Locale.getDefault())
                        .contains(
                            query.toString().lowercase(Locale.getDefault())
                        )
                ) {
                    filteredList.add(item)
                }
            }
        }
        submitList(filteredList)

        return filteredList.isEmpty()
    }

}

private fun setTags(
    context: Context,
    categories: MutableSet<Categories>,
    binding: RecipeCardBinding
) {
    val chipGroup = binding.chipGroup
    chipGroup.removeAllViews()
    categories.forEach { category ->
        val tagName = category.categoryName
        val chip = Chip(context)
        chip.text = tagName
        chip.isCloseIconVisible = false
        chipGroup.addView(chip)
    }
}


