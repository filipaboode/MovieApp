package com.example.movieapp.data.db
import androidx.room.Database
import androidx.room.RoomDatabase

// This is the root Room database for the app, which registers the entities we want Room to persist (users+ favorites).
@Database(entities = [UserEntity::class, FavoriteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
}