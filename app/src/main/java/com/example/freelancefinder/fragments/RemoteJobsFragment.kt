package com.example.freelancefinder.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.freelancefinder.MainActivity
import com.example.freelancefinder.R
import com.example.freelancefinder.adapters.RemoteJobAdapter
import com.example.freelancefinder.api.RetrofitInstance
import com.example.freelancefinder.databinding.FragmentRemoteJobsBinding
import com.example.freelancefinder.utils.Constants
import com.example.freelancefinder.viewmodel.RemoteJobViewModel


class RemoteJobsFragment : Fragment(R.layout.fragment_remote_jobs),
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentRemoteJobsBinding? = null
    private val binding get() = _binding!!
    private lateinit var remoteJobViewModel: RemoteJobViewModel
    private lateinit var jobAdapter: RemoteJobAdapter
    private lateinit var swipeLayout: SwipeRefreshLayout
    private var page = 1
    private var limit = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRemoteJobsBinding.inflate(
            inflater,
            container,
            false
        )

        swipeLayout = binding.swipeContainer
        swipeLayout.setOnRefreshListener(this)
        swipeLayout.setColorSchemeColors(
            Color.GREEN, Color.RED,
            Color.BLUE, Color.CYAN
        )

        swipeLayout.post {
            swipeLayout.isRefreshing = true
            setUpRecyclerView()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remoteJobViewModel = (activity as MainActivity).viewModel

        setUpRecyclerView()
        binding.swipeContainer.setOnRefreshListener {
            fetchingData()
        }
    }

    private fun setUpRecyclerView() {
        jobAdapter = RemoteJobAdapter()
        binding.rvRemoteJobs.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addItemDecoration(
                object : DividerItemDecoration(
                    activity, LinearLayout.VERTICAL
                ) {})
            adapter = jobAdapter
        }
        fetchingData()
    }

    private fun fetchingData() {
        activity?.let {
            if (Constants.isNetworkAvailable(requireActivity())) {

                remoteJobViewModel.remoteJobResult()
                    .observe(viewLifecycleOwner) { remoteJob ->
                        if (remoteJob != null) {
                            jobAdapter.differ.submitList(remoteJob.jobs)
                            swipeLayout.isRefreshing = false
                        }
                    }
            } else {
                Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show()
                swipeLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRefresh() {
        setUpRecyclerView()
    }

}