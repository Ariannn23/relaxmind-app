package com.upn.relaxmind.legacy.xml_fragments.caregiver

/*
 * ─────────────────────────────────────────────────────────────────────────────
 * LEGACY FILE — NO COMPILADO ACTIVAMENTE
 * Este Fragment XML fue reemplazado por CaregiverManageLinksScreen (Jetpack Compose).
 * El NavGraph usa únicamente la versión Compose.
 * Conservado aquí para referencia hasta decidir si se elimina definitivamente.
 *
 * ⚠ NOTA: CaregiverManageLinksFragment NO ha sido testeado.
 *    Valida su funcionamiento antes de decidir si conservarlo o eliminarlo.
 *
 * Dependencias necesarias para activarlo (no están en build.gradle.kts):
 *   - androidx.recyclerview:recyclerview
 *   - androidx.fragment:fragment-ktx
 *   - androidx.lifecycle:lifecycle-viewmodel-ktx
 *   - buildFeatures { viewBinding = true }
 * ─────────────────────────────────────────────────────────────────────────────

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upn.relaxmind.databinding.FragmentCaregiverManageLinksBinding

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

        viewModel.loadLinkedPatients(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = PatientAdapter { patient ->
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
        binding.fabAddPatient.setOnClickListener {
            // findNavController().navigate(R.id.action_manage_to_qr_scanner)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/
