package com.upn.relaxmind.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientCaregiverLinkDao {
    @Query("SELECT * FROM patient_caregiver_links WHERE caregiverId = :caregiverId AND status = 'active'")
    fun getActivePatientsForCaregiver(caregiverId: String): Flow<List<PatientCaregiverLinkEntity>>

    @Query("SELECT * FROM patient_caregiver_links WHERE patientId = :patientId AND status = 'active'")
    fun getActiveCaregiversForPatient(patientId: String): Flow<List<PatientCaregiverLinkEntity>>

    @Query("SELECT * FROM patient_caregiver_links WHERE patientId = :patientId AND status = 'pending'")
    fun getPendingRequestsForPatient(patientId: String): Flow<List<PatientCaregiverLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(link: PatientCaregiverLinkEntity)
    
    @Query("UPDATE patient_caregiver_links SET status = :status WHERE id = :linkId")
    suspend fun updateStatus(linkId: String, status: String)
}
