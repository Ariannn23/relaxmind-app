package com.upn.relaxmind.legacy.xml_fragments.caregiver

/*
 * ─────────────────────────────────────────────────────────────────────────────
 * LEGACY FILE — NO COMPILADO ACTIVAMENTE
 * ViewModel para la versión XML del caregiver (fragment legacy).
 * Reemplazado por la UI Compose en feature/caregiver/ui/.
 *
 * ⚠ NOTA: No ha sido testeado. Conservado solo como referencia.
 *    Hilt removido (@HiltViewModel, @Inject).
 * ─────────────────────────────────────────────────────────────────────────────

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upn.relaxmind.core.data.auth.AuthManager
import com.upn.relaxmind.core.data.models.User

class CaregiverViewModel constructor() : ViewModel() {

    private val _linkedPatients = MutableLiveData<List<User>>()
    val linkedPatients: LiveData<List<User>> = _linkedPatients

    fun loadLinkedPatients(context: Context) {
        val patients = AuthManager.getLinkedUsers(context)
        _linkedPatients.value = patients
    }

    fun unlinkPatient(context: Context, patientId: String) {
        loadLinkedPatients(context)
    }
}
*/
