package com.upn.relaxmind.presentation.caregiver

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upn.relaxmind.data.AuthManager
import com.upn.relaxmind.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CaregiverViewModel @Inject constructor() : ViewModel() {

    private val _linkedPatients = MutableLiveData<List<User>>()
    val linkedPatients: LiveData<List<User>> = _linkedPatients

    fun loadLinkedPatients(context: Context) {
        // En un futuro, esto vendrá de un UseCase -> Repository -> Supabase
        val patients = AuthManager.getLinkedUsers(context)
        _linkedPatients.value = patients
    }

    fun unlinkPatient(context: Context, patientId: String) {
        // Lógica para desvincular (AuthManager ya debería tenerla o se agregará)
        // Tras desvincular, recargamos la lista
        loadLinkedPatients(context)
    }
}
