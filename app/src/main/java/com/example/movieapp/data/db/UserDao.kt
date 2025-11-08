package com.example.movieapp.data.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Dao for user auth tasks
// Very small and checks if an for eg. an email already exists.
@Dao

interface UserDao {

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
}