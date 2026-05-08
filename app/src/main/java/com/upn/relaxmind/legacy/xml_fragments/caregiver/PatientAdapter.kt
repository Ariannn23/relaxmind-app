package com.upn.relaxmind.legacy.xml_fragments.caregiver

/*
 * ─────────────────────────────────────────────────────────────────────────────
 * LEGACY FILE — NO COMPILADO ACTIVAMENTE
 * RecyclerView Adapter para la versión XML del caregiver (fragment legacy).
 * Reemplazado por la UI Compose en feature/caregiver/ui/.
 *
 * ⚠ NOTA: No ha sido testeado. Conservado solo como referencia.
 *
 * Dependencias necesarias para activarlo (no están en build.gradle.kts):
 *   - androidx.recyclerview:recyclerview
 *   - buildFeatures { viewBinding = true }
 * ─────────────────────────────────────────────────────────────────────────────

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upn.relaxmind.core.data.models.User
import com.upn.relaxmind.databinding.ItemPatientLinkBinding

class PatientAdapter(private val onPatientClick: (User) -> Unit) :
    ListAdapter<User, PatientAdapter.PatientViewHolder>(PatientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientLinkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PatientViewHolder(private val binding: ItemPatientLinkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patient: User) {
            binding.tvPatientName.text = "${patient.name} ${patient.lastName}"
            binding.tvWellnessScore.text = "Bienestar: ${patient.wellnessScore}%"
            binding.root.setOnClickListener {
                onPatientClick(patient)
            }
        }
    }

    class PatientDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
*/
