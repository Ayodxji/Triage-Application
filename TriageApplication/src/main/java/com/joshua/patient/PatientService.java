package com.joshua.patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.joshua.location.Location;
import com.joshua.location.LocationService;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomDAO;

public class PatientService {
	private LocationService locationService;
	private SymptomDAO symptomDAO;
	
	public PatientService(LocationService locationService,SymptomDAO symptomDAO) {
		this.locationService = locationService;
		this.symptomDAO = symptomDAO; 
	}
	
	public Patient getPatientData() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the Patient ID");
		String patientId = scanner.nextLine();
		System.out.println("Please Enter First Name");
		String firstName = scanner.nextLine();
		System.out.println("Please Enter LastName");
		String lastName = scanner.nextLine();
		System.out.println("Please Enter PhoneNumber");
		String phoneNumber = scanner.nextLine();
		System.out.println("Please Enter Location");
		Location patientLocation = locationService.getLocationData();
		System.out.println("Please Enter DOB in format yyyy-mm-dd");
		String dobStr = scanner.nextLine();
		LocalDate dob = LocalDate.parse(dobStr);
		Patient patient = new Patient(patientId,firstName,lastName,phoneNumber,patientLocation,dob);
		return patient;
	}
	
	public ArrayList<Symptom> selectPatientSymptoms(){
		Scanner scanner = new Scanner(System.in);
		ArrayList<Symptom> selectedSymptoms = new ArrayList<>();
		ArrayList<Symptom> allSymptoms = symptomDAO.getAllSymptoms();
		System.out.println("Please Select Symptoms. e.g, 1 2 3 4 5");
		for (int i=0;i<allSymptoms.size();i++) {
			System.out.println(i+1+" - "+allSymptoms.get(i).getName());
		}
		String[] selection = scanner.nextLine().split(" ");
		for (String idx: selection) {
			int idxInt = Integer.parseInt(idx.trim())-1;
			selectedSymptoms.add(allSymptoms.get(idxInt));
		}
		return selectedSymptoms;
		
	}

}
