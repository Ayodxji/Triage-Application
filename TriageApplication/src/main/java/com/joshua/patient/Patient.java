package com.joshua.patient;

import java.time.LocalDate;
import java.util.ArrayList;

import com.joshua.location.Location;
import com.joshua.symptom.Symptom;
import com.joshua.user.User;

public class Patient extends User{
	private String patientId;
	private LocalDate dateOfBirth;
	private ArrayList<Symptom> medicalSymptoms;
	private int triageScore;
	
	public Patient(String patientId,String firstName,String lastName,String phoneNumber,Location location,LocalDate dateOfBirth,ArrayList<Symptom> medicalSymptoms,int triageScore) {
		super(firstName,lastName,phoneNumber,location);
		this.patientId = patientId;
		this.medicalSymptoms = medicalSymptoms;
		this.dateOfBirth = dateOfBirth;
		this.triageScore = triageScore;
	}
	
	public Patient(String patientId,String firstName,String lastName,String phoneNumber,Location location,LocalDate dateOfBirth) {
		super(firstName,lastName,phoneNumber,location);
		this.patientId = patientId;
		this.dateOfBirth = dateOfBirth;
	}


	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public ArrayList<Symptom> getMedicalSymptoms() {
		return medicalSymptoms;
	}
	public void setMedicalSymptoms(ArrayList<Symptom> medicalSymptoms) {
		this.medicalSymptoms = medicalSymptoms;
	}
	public void setTriageScore(int triageScore) {
		this.triageScore = triageScore;
	}
	public int getTriageScore() {
		return triageScore;
	}
}
