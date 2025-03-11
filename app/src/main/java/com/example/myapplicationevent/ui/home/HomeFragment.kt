package com.example.myapplicationevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationevent.local.entity.Event // Update this import
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.databinding.FragmentHomeBinding
import com.example.myapplicationevent.di.Injection
import com.example.myapplicationevent.ui.EventAdapter
import com.example.myapplicationevent.ui.factory.ViewModelFactory
import com.google.android.material.carousel.CarouselLayoutManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()

        // Set up SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.refreshEvents() // Trigger the refresh method in ViewModel
        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            rvActiveEvents.layoutManager = CarouselLayoutManager()
            rvFinishedEvents.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupObservers() {
        // Observing Active Events from the repository via ViewModel
        homeViewModel.activeEvents.observe(viewLifecycleOwner) { activeEvents ->
            setEventsData(activeEvents, binding.rvActiveEvents, true)
        }

        // Observing Finished Events from the repository via ViewModel
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { finishedEvents ->
            setEventsData(finishedEvents, binding.rvFinishedEvents, false)
        }

        // Observing Snackbar messages
//        homeViewModel.snackbarText.observe(viewLifecycleOwner) {
//            it.getContentIfNotHandled()?.let { snackbarText ->
//                Snackbar.make(requireView(), snackbarText, Snackbar.LENGTH_SHORT).show()
//            }
//        }

        // Observing loading state
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateLoading(isLoading)
        }
    }

    private fun setEventsData(
        events: List<Event>?, // Changed from EventItem to Event
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        isActiveEvent: Boolean
    ) {
        // Map the list of Event entities to a list of EventItem objects
        val topEventItems = events?.map { it.eventItem }?.take(5) // Extract EventItems and take top 5

        // Create the adapter, passing in the list of EventItems
        val adapter = EventAdapter(
            onItemClick = { eventItem -> navigateToDetailScreen(eventItem) },
            useLayoutA = isActiveEvent
        )

        // Submit the list of EventItems to the adapter
        adapter.submitList(topEventItems)
        recyclerView.adapter = adapter
    }

    private fun navigateToDetailScreen(event: EventItem) {
        val action = HomeFragmentDirections.actionNavigationHomeToDetailEventFragment()
        action.eventId = event.id.toString()
        view?.let {
            androidx.navigation.Navigation.findNavController(it).navigate(action)
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        // Assuming you have two ProgressBars: one for active and one for finished events
        binding.pbActive.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.pbFinished.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.swipeRefreshLayout.isRefreshing = isLoading // Update SwipeRefreshLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
