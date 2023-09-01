package com.andresuryana.aptasari.ui.target

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.TargetAdapter
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.databinding.FragmentTargetBinding
import com.andresuryana.aptasari.util.LearningTarget
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbarError
import com.andresuryana.aptasari.worker.TargetAlarmReceiver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class TargetFragment : Fragment() {

    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TargetViewModel>()

    private lateinit var targetAdapter: TargetAdapter

    private var target: LearningTarget? = null
    private var levelId: String? = null

    @Inject
    lateinit var session: SessionHelper

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) Toast.makeText(
                requireContext(),
                R.string.permission_notification_request,
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTargetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get level id from arguments
        arguments?.getString("level_id")?.let {
            levelId = it
        }

        // Setup adapter
        setupTargetAdapter()

        // Setup button listener
        setupButtonListener()

        // Observe ui state
        observeUiState()

        // Check permission
        checkNotificationPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTargetAdapter() {
        targetAdapter = TargetAdapter()
        targetAdapter.submitList(LearningTarget.values().toList())
        targetAdapter.setOnItemClickListener {
            target = it
            targetAdapter.setSelectedItem(binding.rvTarget, it)
        }
        binding.rvTarget.apply {
            adapter = targetAdapter
            layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        }
    }

    private fun setupButtonListener() {
        binding.btnContinue.text = if (levelId != null) getString(R.string.btn_continue)
        else getString(R.string.btn_save)
        binding.btnContinue.setOnClickListener {
            // Updates directly
            updateTargetDuration()
        }
    }

    private fun updateTargetDuration() {
        target?.let { target ->
            // Update user config
            viewModel.updateUserConfig(
                FirebaseAuth.getInstance().currentUser?.uid,
                true,
                target.duration
            )
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

        // Is user config updated
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isUpdated.collectLatest {
                    // After update success, start the target learning alarm
                    startTargetAlarm(requireContext())

                    // Set variable to indicates users first quiz launch
                    session.setUserFirstQuiz(false)

                    // Show message success
                    showSnackbar(R.string.success_set_target_alarm)

                    // If levelId is not null, it means next navigation is quiz fragment
                    // otherwise, it means accessed from profile/setting
                    if (levelId != null) {
                        // Navigate to quiz fragment
                        findNavController().navigate(TargetFragmentDirections.navigateToQuiz(levelId!!))
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PermissionChecker.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun startTargetAlarm(context: Context) {
        // Get alarm manager from service
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Create alarm pending intent
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            TargetAlarmReceiver.TARGET_NOTIFICATION_ID,
            Intent(context, TargetAlarmReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Set alarm to start at 12:00 p.m. (Noon)
        val triggerTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Repeat alarm with an interval of 1 day
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            triggerTime,
//            AlarmManager.INTERVAL_DAY,
//            alarmIntent
//        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerTime, alarmIntent),
            alarmIntent
        )
    }
}