package com.example.todoapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todo")
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val todoTitle: String,
    val todoDesc: String,
    val date: String,
    val time: String,
    var stat: String,
    val priori: String

): Parcelable