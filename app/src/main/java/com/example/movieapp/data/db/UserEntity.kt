package com.example.movieapp.data.db
import androidx.room.Entity
import androidx.room.PrimaryKey

// User table for the local auth
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)