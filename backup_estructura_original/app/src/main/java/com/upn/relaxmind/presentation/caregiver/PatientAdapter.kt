package com.upn.relaxmind.presentation.caregiver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upn.relaxmind.data.models.User
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
            
            // Aquí se podría cargar la imagen con Glide o Coil si hubiera avatar
            
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
