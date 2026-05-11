package com.upn.relaxmind.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CheckInEntity::class,
        DiaryEntryEntity::class,
        SyncQueueEntity::class,
        ProfileEntity::class,
        PatientCaregiverLinkEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun profileDao(): ProfileDao
    abstract fun patientCaregiverLinkDao(): PatientCaregiverLinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "relaxmind_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
