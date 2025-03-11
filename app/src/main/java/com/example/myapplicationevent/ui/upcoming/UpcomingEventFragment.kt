package com.example.myapplicationevent.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationevent.local.entity.Event
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.databinding.FragmentUpcomingEventBinding
import com.example.myapplicationevent.di.Injection
import com.example.myapplicationevent.ui.EventAdapter
import com.example.myapplicationevent.ui.factory.ViewModelFactory
import com.example.myapplicationevent.ui.home.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupRecyclerView()

        // Set up SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.refreshEvents() // Trigger the refresh method in ViewModel
        }
    }

    private fun setupObserver() {
        homeViewModel.activeEvents.observe(viewLifecycleOwner) {
            setEventsData(it, binding.rvEventActive)
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            updateLoading(it, binding.pbActive)
        }
        homeViewModel.snackbarText.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let{snackbarText->
                Snackbar.make(requireView(), snackbarText, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setEventsData(
        events: List<Event>?,
        recyclerView: androidx.recyclerview.widget.RecyclerView,
    ) {
        val adapter = EventAdapter(onItemClick = { event ->
            navigateToDetailScreen(event)
        }, useLayoutA = false)
        adapter.submitList(events?.map { it.eventItem })
        recyclerView.adapter = adapter
    }

    private fun navigateToDetailScreen(event: EventItem) {
        val action = UpcomingEventFragmentDirections.actionNavigationUpcomingEventToDetailEventFragment()
        action.eventId = event.id.toString()
        view?.let{
            Navigation.findNavController(requireView()).navigate(action)
        }
    }


    private fun updateLoading(isLoading: Boolean, progressBar: android.widget.ProgressBar) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.swipeRefreshLayout.isRefreshing = isLoading // Update SwipeRefreshLayout
    }

    private fun setupRecyclerView() {
        with(binding) {
            rvEventActive.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}