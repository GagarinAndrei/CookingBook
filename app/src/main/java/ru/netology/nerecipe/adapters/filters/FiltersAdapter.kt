package ru.netology.nerecipe.adapters.filters

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.FilterBinding
import ru.netology.nerecipe.dto.Categories


class FiltersAdapter(

    private val interactionListener: FilterInteractionListener,
    private val sharedPref: SharedPreferences?,
    private val lifecycleOwner: LifecycleOwner

) : ListAdapter<String, FiltersAdapter.FilterViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FilterBinding.inflate(inflater, parent, false)
        return FilterViewHolder(binding, interactionListener, sharedPref, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class FilterViewHolder(
        private val binding: FilterBinding,
        private val interactionListener: FilterInteractionListener,
        private val sharedPref: SharedPreferences?,
        private val lifecycleOwner: LifecycleOwner

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var category: String

        init {
            binding.filterCategoryCheckBox.setOnClickListener {
                if (binding.filterCategoryCheckBox.isChecked) {
                    if (sharedPref != null) {
                        with(sharedPref.edit()) {
                            putBoolean(adapterPosition.toString(), true)
                            apply()
                        }
                    }
                    interactionListener.onCheckClicked(Categories.from(category))
                    interactionListener.filterCheckboxUpdate.value = true
                } else {
                    if (sharedPref != null) {
                        with(sharedPref.edit()) {
                            putBoolean(adapterPosition.toString(), false)
                            apply()
                        }
                    }
                    interactionListener.onUncheckClicked(Categories.from(category))
                    interactionListener.filterCheckboxUpdate.value = true
                }
            }
        }

        fun bind(category: String) {
            this.category = category
            with(binding) {
                filterCategoryTextView.text = category

                interactionListener.filterCheckboxUpdate.observe(lifecycleOwner) {
                    if (sharedPref != null) {
                        if (sharedPref.getBoolean(adapterPosition.toString(), false)) {
                            filterCategoryCheckBox.isChecked = true
                        } else if (!sharedPref.getBoolean(adapterPosition.toString(), false)) {
                            filterCategoryCheckBox.isChecked = false
                        }
                    }
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