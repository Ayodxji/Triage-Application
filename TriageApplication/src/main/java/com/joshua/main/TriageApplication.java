package com.joshua.main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.joshua.assignment.AssignmentDAO;
import com.joshua.assignment.AssignmentService;
import com.joshua.location.LocationDAO;
import com.joshua.location.LocationService;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseDAO;
import com.joshua.nurse.NurseService;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;
import com.joshua.patient.PatientService;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomDAO;
import com.joshua.triage.TriageService;

public class TriageApplication {
	public static void main(String[] args) {

		SymptomDAO symptomDAO = new SymptomDAO();
		LocationService locationService = new LocationService();
		LocationDAO locationDAO = new LocationDAO();
		PatientDAO patientDAO = new PatientDAO(locationDAO, symptomDAO);
		PatientService patientService = new PatientService(locationService, symptomDAO);
		NurseService nurseService = new NurseService(locationService);
		NurseDAO nurseDAO = new NurseDAO(locationDAO);
		TriageService triageService = new TriageService(locationService);
		AssignmentService assignmentService = new AssignmentService(triageService, patientDAO, nurseDAO);
		AssignmentDAO assignmentDAO = new AssignmentDAO(nurseDAO, patientDAO);

		String Menu = """
				[1] : Select 1 to add Nurse.
				[2] : Select 2 to add Patient.
				[3] : Select 3 to assign symptoms to patients
				[4] : Select 4 to assign Patient to Nurse.
				[6] : Select 6 to exit.
				""";

		String choice;
		Scanner scanner = new Scanner(System.in);

		do {
			System.out.println(Menu);
			choice = scanner.nextLine();
			switch (choice) {
				case "1":
					Nurse nurseData = nurseService.getNurseData();
					if (nurseDAO.addNurse(nurseData, nurseData.getLocation())) {
						System.out.println("Nurse " + nurseData.getNurseId() + " added successfully");
					} else {
						System.out.println("Nurse Creation Failed");
					}
					break;
				case "2":
					Patient patientData = patientService.getPatientData();
					if (patientDAO.addPatient(patientData, patientData.getLocation())) {
						System.out.println("Patient " + patientData.getPatientId() + " added successfully");
					} else {
						System.out.println("Patient Creation Failed");
					}
					break;
				case "3":
					System.out.println("Please Enter Patient ID");
					String patientId = scanner.nextLine();
					Patient patient = patientDAO.findPatient(patientId);
					if (patient != null) {
						System.out.println("Patient with id " + patient.getPatientId() + " selected");
						ArrayList<Symptom> patientSymptom = patientService.selectPatientSymptoms();
						patient.setMedicalSymptoms(patientSymptom);
						if (patientDAO.assignSymptomsToPatient(patient)) {
							System.out.println("Symptoms have been assigned");
						} else {
							System.out.println("Adding Symptom Failed");
						}
					} else {
						System.out.println("Patient not Found");
					}
					break;
				case "4":
					System.out.println("Assigning patients to nurses...");
					long start = System.nanoTime();
					ArrayList<Nurse> availableNurses = nurseDAO.getAllAvailableNurse();
					ArrayList<Patient> patients = patientDAO.getAllPatients();
					for (Patient p : patients) {
						int score = triageService.calculateTriageLevel(p);
						p.setTriageScore(score);
					}

					Map<Nurse, ArrayList<Patient>> assignmentMap =
						assignmentService.assignNurseToPatient(availableNurses, patients);
					long end = System.nanoTime();
					long duration = end - start;

					System.out.println("Assignment completed in " + (duration / 1_000_000) + " ms");
					Set<Nurse> assignedNurses = assignmentMap.keySet();
					for (Nurse nurse : assignedNurses) {
						System.out.println(nurse.getNurseId() + " assigned to:");
						for (Patient assignedPatient : assignmentMap.get(nurse)) {
							System.out.println(" - " + assignedPatient.getPatientId());
						}
						System.out.println();
					}

					if (assignmentDAO.saveAssignments(assignmentMap)) {
						System.out.println("Assignments saved to database.");
					} else {
						System.out.println("Warning: assignments could not be persisted.");
					}
					break;
				default:
					if (!choice.equals("6")) {
						System.out.println("Invalid option. Please choose 1-4 or 6 to exit.");
					}
			}
		} while (!choice.equals("6"));

		scanner.close();
	}
}
