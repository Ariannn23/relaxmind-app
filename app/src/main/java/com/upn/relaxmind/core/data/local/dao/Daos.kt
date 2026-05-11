package com.upn.relaxmind.core.data.local.dao

import androidx.room.*
import com.upn.relaxmind.core.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM profiles")
    suspend fun clearAll()
}

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE user_id = :userId ORDER BY created_at DESC")
    fun getDiaryEntries(userId: String): Flow<List<DiaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntity)

    @Query("SELECT * FROM diary_entries WHERE is_synced = 0")
    suspend fun getUnsyncedEntries(): List<DiaryEntity>

    @Update
    suspend fun updateEntry(entry: DiaryEntity)
    
    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE user_id = :userId")
    fun getReminders(userId: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE is_synced = 0")
    suspend fun getUnsyncedReminders(): List<ReminderEntity>
    
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: String)
}
