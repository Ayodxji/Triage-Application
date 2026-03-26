package com.joshua.triage;

import java.util.ArrayList;

import com.joshua.location.LocationService;
import com.joshua.nurse.Nurse;
import com.joshua.patient.Patient;
import com.joshua.symptom.Symptom;

public class TriageService {
	private static final double FUEL_PRICE_PER_LITRE = 1.6;
	private static final double URGENCY_WEIGHT = 3;
	private static final double FUEL_WEIGHT = 1.5;
	private static final double EXPERIENCE_WEIGHT = 0.5;
	private LocationService locationService;
	
	public TriageService() {
		
	}
	
	public TriageService(LocationService locationService) {
		this.locationService = locationService;
	}

	public double calculateCost(Nurse nurse,Patient patient) {
		double distance = locationService.calculateDistance(nurse.getLocation(), patient.getLocation());
		double fuelUsed = distance*Nurse.FUEL_CONSUMPTION;
		double fuelCost = fuelUsed*FUEL_PRICE_PER_LITRE;
		int urgencyLevel = calculateTriageLevel(patient);
		int experienceLevel = nurse.getYearsOfExperience();
		
		double totalCost = 
                (FUEL_WEIGHT * fuelCost) 
                -(URGENCY_WEIGHT * urgencyLevel)
                - (EXPERIENCE_WEIGHT * experienceLevel* urgencyLevel);
        
        return totalCost;
	}
	
	
	public int calculateTriageLevel(Patient patient) {
		ArrayList<Symptom> symptoms = patient.getMedicalSymptoms();
		// A null or empty symptom list does not mean the patient is low priority —
		// symptoms may simply not have been recorded yet. Returning 0 would push
		// them below every patient with any symptom; use NONURGENT (1) as a safe default.
		if (symptoms == null || symptoms.isEmpty()) {
			System.out.println("Warning: Patient " + patient.getPatientId() + " has no recorded symptoms. Defaulting to NONURGENT (1).");
			return 1;
		}
		int maxUrgencyLevel = 0;
		for (Symptom medicalSymptom: symptoms) {
			if (medicalSymptom.getSymptomPriority().getUrgencyValue()>maxUrgencyLevel) {
				maxUrgencyLevel = medicalSymptom.getSymptomPriority().getUrgencyValue();
			}
		}return maxUrgencyLevel;
	}
}
