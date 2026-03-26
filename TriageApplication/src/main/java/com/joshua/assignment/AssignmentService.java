package com.joshua.assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseDAO;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;
import com.joshua.triage.TriageService;

public class AssignmentService {
	private static final int MAX_PATIENTS_PER_NURSE = 5;
	private TriageService triageService;
	private PatientDAO patientDAO;
	private NurseDAO nurseDAO;
	
	public AssignmentService(TriageService triageService,PatientDAO patientDAO,NurseDAO nurseDAO) {
		this.triageService = triageService;
		this.patientDAO = patientDAO;
		this.nurseDAO = nurseDAO;
	}
	
	public Map<Nurse,ArrayList<Patient>> assignNurseToPatient(ArrayList<Nurse> availableNurse,ArrayList<Patient> patients) {
		Map<Nurse,ArrayList<Patient>> assignmentMap = new HashMap<>();
		// Sort patients by triage score descending so the most urgent are assigned first.
		// Without this, a non-urgent patient processed early can claim the best nurse
		// before a higher-priority patient gets a turn.
		patients.sort((a, b) -> Integer.compare(b.getTriageScore(), a.getTriageScore()));
		for (Patient patient: patients) {
			double minCost = Double.MAX_VALUE;
			Nurse bestNurse = null;
			for (Nurse nurse:availableNurse) {
				ArrayList<Patient> assignedPatients = assignmentMap.getOrDefault(nurse, new ArrayList<Patient>());
				if (assignedPatients.size()>=AssignmentService.MAX_PATIENTS_PER_NURSE) {
					// Capacity reached — skip nurse. Do NOT mutate nurse availability here:
					// the mutation has no effect on this check and corrupts in-memory state
					// if the algorithm is called more than once in a session.
					continue;
				}
				double cost = triageService.calculateCost(nurse, patient);
				if (cost<minCost) {
					minCost = cost;
					bestNurse = nurse;
				}
			}
			if (bestNurse != null) {
				assignmentMap.computeIfAbsent(bestNurse, k -> new ArrayList<>()).add(patient);
			}else {
				System.out.println("No Nurse Available for Patient "+patient.getFirstName());
			}
		}return assignmentMap;
	}
}
