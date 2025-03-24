package com.example.myapplicationevent.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationevent.databinding.FragmentFavoriteBinding
import com.example.myapplicationevent.local.entity.Event
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.di.Injection
import com.example.myapplicationevent.ui.EventAdapter
import com.example.myapplicationevent.ui.factory.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupRecyclerView()
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

    private fun setupObserver() {
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                setEventsData(events, binding.rvEventFavorite)
            } else {
                binding.tvNoFavorite.visibility = View.GONE
                binding.tvNoFavorite.visibility = View.VISIBLE
            }
        }
    }


    private fun navigateToDetailScreen(event: EventItem) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToNavigationDetailEvent()
        action.eventId = event.id.toString()
        view?.let {
            Navigation.findNavController(it).navigate(action)
        }
    }


    private fun setupRecyclerView() {
        with(binding) {
            rvEventFavorite.layoutManager = LinearLayoutManager(context)
        }
    }


}