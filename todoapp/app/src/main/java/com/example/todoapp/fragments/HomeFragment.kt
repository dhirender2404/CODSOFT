package com.example.todoapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.todoapp.OneActivity
import com.example.todoapp.R
import com.example.todoapp.adapter.TodoAdapter
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.model.Todo
import com.example.todoapp.viewmodel.TodoViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider,
    TodoAdapter.DbUpdateListener {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        todoViewModel = (activity as OneActivity).todoViewModel
        setupHomeRecyclerView()
        binding.addTodoFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addTodoFragment)

        }


    }

    private fun updateUI(todo: List<Todo>?) {
        if (todo != null) {
            if (todo.isNotEmpty()) {
                binding.emptyTodoImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyTodoImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupHomeRecyclerView() {
        todoAdapter = TodoAdapter(this)
        binding.homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                context,
                VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = todoAdapter
        }
        activity?.let {
            todoViewModel.getAllTodo().observe(viewLifecycleOwner) { todo ->
                todoAdapter.differ.submitList(todo)
                updateUI(todo)
            }
        }

    }






    private fun searchTodo(query: String?) {
        val searchQuery = "%$query"
        todoViewModel.searchTodo(searchQuery).observe(this) { list ->
            todoAdapter.differ.submitList(list)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchTodo(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    override fun updateTodo(newItem: Todo) {
        todoViewModel?.updateTodo(newItem)
    }
}