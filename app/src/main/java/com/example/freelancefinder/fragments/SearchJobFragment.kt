package com.example.freelancefinder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freelancefinder.MainActivity
import com.example.freelancefinder.R
import com.example.freelancefinder.adapters.RemoteJobAdapter
import com.example.freelancefinder.databinding.FragmentSearchJobBinding
import com.example.freelancefinder.utils.Constants
import com.example.freelancefinder.viewmodel.RemoteJobViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchJobFragment : Fragment(R.layout.fragment_search_job) {

    private var _binding: FragmentSearchJobBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RemoteJobViewModel
    private lateinit var jobAdapter: RemoteJobAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchJobBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        if (Constants.isNetworkAvailable(requireContext())) {
            searchJob()
        } else {
            Toast.makeText(activity,"No internet connection", Toast.LENGTH_SHORT).show()
        }
    }


    private fun searchJob() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchJob(editable.toString())
                    }
                }
            }
        }
        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        jobAdapter = RemoteJobAdapter()
        binding.rvSearchJobs.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = jobAdapter
        }
        viewModel.searchResult().observe(viewLifecycleOwner) { remoteJob ->
            jobAdapter.differ.submitList(remoteJob.jobs)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}