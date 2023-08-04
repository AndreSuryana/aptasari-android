package com.andresuryana.aptasari.ui.target

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.andresuryana.aptasari.R
import com.andresuryana.aptasari.adapter.TargetAdapter
import com.andresuryana.aptasari.databinding.FragmentTargetBinding
import com.andresuryana.aptasari.di.AppModule
import com.andresuryana.aptasari.util.LearningTarget
import com.andresuryana.aptasari.util.SnackbarUtils.showSnackbar
import com.andresuryana.aptasari.worker.TargetAlarmHelper.startLearningTargetAlarm
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TargetFragment : Fragment() {

    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TargetViewModel>()

    private lateinit var targetAdapter: TargetAdapter

    private var target: LearningTarget? = null
    private var levelId: String? = null

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
        binding.btnContinue.setOnClickListener {
            // Init session helper
            val session = AppModule.provideSessionHelper(requireContext().applicationContext)

            target?.let { target ->
                // Update user config
                viewModel.updateUserConfig(
                    FirebaseAuth.getInstance().currentUser?.uid,
                    true,
                    target.duration
                )
                startLearningTargetAlarm(requireContext())
                session.setUserFirstQuiz(false)
                showSnackbar(R.string.success_set_target_alarm)
            }

            // Navigate to quiz fragment
            levelId?.let {
                findNavController().navigate(TargetFragmentDirections.navigateToQuiz(it))
            }
        }
    }
}