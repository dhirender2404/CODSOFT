package com.example.todoapp.repository

import com.example.todoapp.database.TodoDatabase
import com.example.todoapp.model.Todo

class TodoRepository(private val db: TodoDatabase) {
    suspend fun insertTodo(todo: Todo) = db.getTodoDao().insertTodo(todo)
    suspend fun deleteTodo(todo: Todo) = db.getTodoDao().deleteTodo(todo)
    suspend fun updateTodo(todo: Todo) = db.getTodoDao().updateTodo(todo)

    fun getAllTodo() = db.getTodoDao().getAllTodo()
    fun searchTodo(query: String?)= db.getTodoDao().searchTodo(query)
}