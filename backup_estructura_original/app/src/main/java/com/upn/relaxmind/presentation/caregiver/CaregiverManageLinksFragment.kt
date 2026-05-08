package com.upn.relaxmind.presentation.caregiver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upn.relaxmind.databinding.FragmentCaregiverManageLinksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaregiverManageLinksFragment : Fragment() {

    private var _binding: FragmentCaregiverManageLinksBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CaregiverViewModel by viewModels()
    private lateinit var adapter: PatientAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaregiverManageLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()

        // Cargar datos iniciales
        viewModel.loadLinkedPatients(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = PatientAdapter { patient ->
            // Acción al tocar un paciente (ej: ir a CAR-04 Perfil del Paciente)
            // findNavController().navigate(R.id.action_to_patient_detail, bundle)
        }
        
        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.linkedPatients.observe(viewLifecycleOwner) { patients ->
            if (patients.isEmpty()) {
                binding.lytEmpty.visibility = View.VISIBLE
                binding.rvPatients.visibility = View.GONE
            } else {
                binding.lytEmpty.visibility = View.GONE
                binding.rvPatients.visibility = View.VISIBLE
                adapter.submitList(patients)
            }
        }
    }

    private fun setupClickListeners() {
        // En XML, el botón de retroceso suele estar en el Toolbar o ser un botón custom
        // Si tienes un botón de atrás en el layout, conéctalo aquí:
        // binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddPatient.setOnClickListener {
            // Navegar a la pantalla de escanear QR (CAR-07)
            // findNavController().navigate(R.id.action_manage_to_qr_scanner)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
