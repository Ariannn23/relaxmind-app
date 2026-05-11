package com.upn.relaxmind.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE tempLinkCode = :code AND tempLinkCodeExpiresAt > :currentTime LIMIT 1")
    suspend fun getProfileByValidCode(code: String, currentTime: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ProfileEntity)
}
