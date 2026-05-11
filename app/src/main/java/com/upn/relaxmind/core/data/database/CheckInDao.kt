package com.upn.relaxmind.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY date DESC")
    fun getAllForUser(userId: String): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckInEntity)

    @Query("DELETE FROM check_ins WHERE id = :id")
    suspend fun delete(id: String)
    
    @Query("DELETE FROM check_ins")
    suspend fun clearAll()
}
