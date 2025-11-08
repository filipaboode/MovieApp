package com.example.movieapp.data.db

import android.content.Context
import androidx.room.Room

// A singleton holder for the Room database
object DatabaseProvider {
    @Volatile private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase =
        instance?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "cineshare.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it}
        }
}