package com.example.movieapp.data.db

import androidx.room.*

/**
 * Table that stores "user X likes movie Y" plus some movie info for quick display
 * We also keep title/poster/vote locally so the favorites screen can show something
 * even if we don’t re-fetch from the API
 */
@Entity(
    tableName = "favorites",
            indices = [Index(value = ["userId", "movieId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]

)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val userId: Long,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double
)
