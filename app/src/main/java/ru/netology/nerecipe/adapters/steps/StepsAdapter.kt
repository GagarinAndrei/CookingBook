package ru.netology.nerecipe.adapters.steps

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.StepBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.ui.EditRecipeFragment
import ru.netology.nerecipe.ui.NewRecipeFragment
import ru.netology.nerecipe.ui.RecipeFragment

class StepsAdapter(

    private val recipe: Recipe,
    private val stepsFragment: String,
    private val interactionListener: StepInteractionListener

) : ListAdapter<String, StepsAdapter.StepViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepBinding.inflate(inflater, parent, false)
        return StepViewHolder(binding, stepsFragment, interactionListener)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(recipe, getItem(position))
    }


    class StepViewHolder(
        private val binding: StepBinding,
        private val stepsFragment: String,
        private val interactionListener: StepInteractionListener

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var step: String
        private lateinit var recipe: Recipe
        private val caller = when (stepsFragment) {
            NewRecipeFragment.CALLER_NEW_RECIPE -> NewRecipeFragment.CALLER_NEW_RECIPE
            EditRecipeFragment.CALLER_EDIT_RECIPE -> EditRecipeFragment.CALLER_EDIT_RECIPE
            else -> RecipeFragment.CALLER_RECIPE
        }

        init {
            binding.deleteStepMaterialButton.setOnClickListener {
                interactionListener.onDeleteStepButtonClicked(recipe, step, caller)
            }
        }

        fun bind(recipe: Recipe, step: String) {
            this.recipe = recipe
            this.step = step
            val stepNumber = adapterPosition + 1
            with(binding) {
                stepTextView.text = step
                stepNumberTextView.text = stepNumber.toString()
                deleteStepMaterialButton.visibility = View.GONE

                if (recipe.steps.getValue(step).isNotEmpty()) {
                    stepImageView.setImageURI(Uri.parse(recipe.steps.getValue(step)))
                    stepImageView.visibility = View.VISIBLE
                } else {
                    stepImageView.visibility = View.GONE
                }

                if (stepsFragment != RecipeFragment.CALLER_RECIPE) {
                    deleteStepMaterialButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

    }

}