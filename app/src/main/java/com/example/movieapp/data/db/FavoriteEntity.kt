package com.example.movieapp.data.db

import androidx.room.*

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
