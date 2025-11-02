package com.example.movieapp.data.db
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, FavoriteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
}