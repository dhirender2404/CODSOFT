package com.example.todoapp.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.model.Todo

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM TODO ORDER BY id DESC")
    fun getAllTodo(): LiveData<List<Todo>>

    @Query("SELECT * FROM TODO WHERE todoTitle LIKE :query OR todoDesc LIKE :query")
    fun searchTodo(query: String?): LiveData<List<Todo>>

}