package com.andresuryana.aptasari.ui.profile

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.databinding.FragmentProfileBinding
import com.andresuryana.aptasari.databinding.ItemSettingBinding
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import com.andresuryana.aptasari.worker.TargetAlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate menu
        inflateMenu()

        // Observe ui state
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }

    private fun inflateMenu() {
        // Loop through menu item in the parent layout
        SettingMenu.values().forEach { menu ->
            // Add menu into menu container
            val itemMenuView = ItemSettingBinding.inflate(layoutInflater)

            // Set menu data
            itemMenuView.ivIcon.setImageResource(menu.icon)
            itemMenuView.tvTitle.setText(menu.title)

            // Set click listener
            itemMenuView.root.setOnClickListener { onMenuItemClickListener(menu) }

            // Add menu into menu container
            binding.menuContainer.addView(itemMenuView.root)
        }
    }

    private fun onMenuItemClickListener(menu: SettingMenu) {
        when (menu) {
            SettingMenu.ACCOUNT -> {
                findNavController().navigate(R.id.navigateToAccountFragment)
            }
            SettingMenu.LEARNING_TARGET -> {
                findNavController().navigate(R.id.targetFragment)
            }
            SettingMenu.LOGOUT -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.title_logout)
                    .setMessage(R.string.question_logout)
                    .setCancelable(false)
                    .setPositiveButton(R.string.btn_positive) { _, _ ->
                        viewModel.logout()
                    }
                    .setNegativeButton(R.string.btn_negative) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun observeUiState() {
        // Error
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isError.collectLatest { messagePair ->
                    withContext(Dispatchers.Main) {
                        showSnackbarError(messagePair)
                    }
                }
            }
        }

        // Register Action
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutAction.collectLatest {
                    withContext(Dispatchers.Main) {
                        // Make sure disable alarm work manager if user logged out
                        cancelTargetAlarm(requireContext())

                        // Navigate to on boarding fragment
                        val options = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(R.id.app_navigation, true)
                            .build()
                        findNavController().navigate(R.id.onboardingFragment, null, options)
                    }
                }
            }
        }
    }

    private fun cancelTargetAlarm(context: Context) {
        // Get alarm manager from service
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Get target alarm intent
        @SuppressLint("UnspecifiedImmutableFlag")
        val alarmIntent = PendingIntent.getService(
            context,
            TargetAlarmReceiver.TARGET_NOTIFICATION_ID,
            Intent(context, TargetAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel alarm if there is target alarm running
        if (alarmIntent != null)
            alarmManager.cancel(alarmIntent)
    }
}