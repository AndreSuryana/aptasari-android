package com.andresuryana.aptasari.ui.target

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.andresuryana.aptasari.adapter.TargetAdapter
import com.andresuryana.aptasari.databinding.FragmentTargetBinding
import com.andresuryana.aptasari.util.LearningTarget
import com.andresuryana.aptasari.worker.TargetAlarmReceiver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TargetFragment : Fragment() {

    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TargetViewModel>()

    private lateinit var targetAdapter: TargetAdapter

    private var target: LearningTarget? = null

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

        // Setup adapter
        setupTargetAdapter()

        // Setup button listener
        setupButtonListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTargetAdapter() {
        targetAdapter = TargetAdapter()
        targetAdapter.setList(LearningTarget.values().toList())
        targetAdapter.setOnItemClickListener {
            target = it
        }
        binding.rvTarget.apply {
            adapter = targetAdapter
            layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        }
    }

    private fun setupButtonListener() {
        binding.btnContinue.setOnClickListener {
            target?.let { target ->
                // Update user config
                viewModel.updateUserConfig(
                    FirebaseAuth.getInstance().currentUser?.uid,
                    true,
                    target.duration
                )
                setLearningTargetAlarm(target)
            }
        }
    }

    private fun setLearningTargetAlarm(target: LearningTarget?) {
        // Create alarm work manager
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireActivity(), TargetAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Set alarm to repeat everyday
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}