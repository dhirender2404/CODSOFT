package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.TodoRepository

class TodoViewModelFactory(val app: Application, private val
todoRepository: TodoRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(app, todoRepository) as T
    }
}