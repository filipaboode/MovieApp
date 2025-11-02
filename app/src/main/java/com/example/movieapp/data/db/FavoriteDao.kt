package com.example.movieapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT movieId FROM favorites WHERE userId = :userId")
    fun favoriteIdsFlow(userId: Long): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fav: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND movieId = :movieId")
    suspend fun delete(userId: Long, movieId: Int)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getAll(userId: Long): List<FavoriteEntity>

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY id DESC")
    fun favoritesFlow(userId: Long): Flow<List<FavoriteEntity>>
}