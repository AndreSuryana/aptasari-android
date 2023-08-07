package com.andresuryana.aptasari

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.andresuryana.aptasari.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainMenuList = listOf(R.id.levelFragment, R.id.profileFragment)

    private lateinit var navController: NavController

    private var isUserInteraction: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout binding
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment change listener
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (mainMenuList.contains(destination.id)) {
                binding.bottomNav.visibility = View.VISIBLE
                if (!isUserInteraction) {
                    if (destination.id == R.id.levelFragment)
                        binding.bottomNav.selectedItemId = R.id.menuLevel
                    else if (destination.id == R.id.profileFragment)
                        binding.bottomNav.selectedItemId = R.id.menuProfile
                }
            } else {
                binding.bottomNav.visibility = View.GONE
            }
            isUserInteraction = false
        }

        // Setup bottom navigation view
        binding.bottomNav.setOnItemSelectedListener {
            isUserInteraction = true // Set the flag to true to indicate user interaction
            when (it.itemId) {
                R.id.menuLevel -> {
                    navController.navigate(R.id.levelFragment)
                }

                R.id.menuProfile -> {
                    navController.navigate(R.id.profileFragment)
                }
            }
            true
        }

        binding.bottomNav.setOnItemReselectedListener {
            // Do nothing! Required!
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val prev = navController.currentDestination
        val curr = navController.previousBackStackEntry?.destination

        // Handle back pressed on quizFragment
        if (prev?.id == R.id.quizFragment) {
            AlertDialog.Builder(this)
                .setTitle(R.string.title_quiz_exit)
                .setMessage(R.string.subtitle_quiz_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_positive) { _, _ ->
                    navController.popBackStack()
                }
                .setNegativeButton(R.string.btn_negative) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }

        // Check if current is fragmentLevel
        if (curr?.id == R.id.levelFragment) super.onBackPressed()

        // Check if the current destination is in the mainMenuList and not the fragmentLevel
        if (prev != null && curr != null && mainMenuList.contains(curr.id) && prev.id != R.id.levelFragment) {
            // Pop the back stack to fragmentLevel
            navController.popBackStack(R.id.levelFragment, false)
        } else {
            // If not in mainMenuList or fragmentLevel, proceed with normal back button behavior
            super.onBackPressed()
        }
    }
}