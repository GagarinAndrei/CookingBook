package ru.netology.nerecipe

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.netology.nerecipe.databinding.ActivityMainBinding
import ru.netology.nerecipe.dto.Categories
import ru.netology.nerecipe.ui.FiltersFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController
        binding.recipesBottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.recipesFeedFragment || destination.id == R.id.faveRecipesFeedFragment) {
                binding.recipesBottomNavigationView.visibility = View.VISIBLE
            } else {
                binding.recipesBottomNavigationView.visibility = View.GONE
            }
        }

        val sharedPref =
            getSharedPreferences(FiltersFragment.SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                for (i in 0 until Categories.values().size) {
                    putBoolean(i.toString(), false)
                }
                apply()
            }
        }

    }
}