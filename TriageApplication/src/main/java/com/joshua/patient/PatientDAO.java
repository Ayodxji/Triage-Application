package com.joshua.patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.joshua.databaseconnection.DatabaseConnection;
import com.joshua.location.Location;
import com.joshua.location.LocationDAO;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomDAO;
import com.joshua.symptom.SymptomPriority;
import com.joshua.triage.TriageService;

public class PatientDAO {
	private LocationDAO locationDAO;
	private SymptomDAO symptomDAO;
	private TriageService triageService;
	
	public PatientDAO(LocationDAO locationDAO,SymptomDAO symptomDAO) {
		this.locationDAO = locationDAO;
		this.symptomDAO = symptomDAO;
		this.triageService = triageService;
		
	}
	public boolean addPatient(Patient patient,Location location) {
		String queryToAddLocation = "INSERT INTO location (latitude, longitude) VALUES (?, ?)";
		String queryToAddPatient = "INSERT INTO patient (patient_id, first_name, last_name, phone_number, location_id, date_of_birth)"
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		// Both inserts run inside a single transaction so a failed patient insert
		// cannot leave an orphaned location row.
		try(Connection con = DatabaseConnection.getDBConnection()){
			con.setAutoCommit(false);
			int locationId = locationDAO.addLocation(location);
			if (locationId < 0) {
				con.rollback();
				System.out.println("Failed to insert location");
				return false;
			}
			try(PreparedStatement addPatientPST = con.prepareStatement(queryToAddPatient)){
				addPatientPST.setString(1, patient.getPatientId());
				addPatientPST.setString(2, patient.getFirstName());
				addPatientPST.setString(3, patient.getLastName());
				addPatientPST.setString(4, patient.getPhoneNumber());
				addPatientPST.setInt(5, locationId);
				addPatientPST.setDate(6, java.sql.Date.valueOf(patient.getDateOfBirth()));
				int rowsAffected = addPatientPST.executeUpdate();
				con.commit();
				return rowsAffected > 0;
			}catch(SQLException e) {
				con.rollback();
				System.out.println(e.getMessage());
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}return false;
	}
	
	public boolean assignSymptomsToPatient(Patient patient) {
		String assignSymptomQuery = "INSERT INTO patient_symptoms (patient_id, symptom_id) VALUES (?, ?)";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement assignSymptomPST = con.prepareStatement(assignSymptomQuery)){
			for (Symptom symptom:patient.getMedicalSymptoms()) {
				int symptomId = symptomDAO.getSymptomIdFromName(symptom.getName());
				assignSymptomPST.setString(1, patient.getPatientId());
				assignSymptomPST.setInt(2, symptomId);
				assignSymptomPST.addBatch();
			}
			int[] batchResult = assignSymptomPST.executeBatch();
			return batchResult.length>0;
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public Patient findPatient(String id) {
		String queryToFindPatient = "SELECT * FROM patient WHERE patient_id = ?";
		try (Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement findPatientPST = con.prepareStatement(queryToFindPatient)){
			findPatientPST.setString(1, id);
			ResultSet rs = findPatientPST.executeQuery();
			if (rs.next()) {
				String patientId = rs.getString("patient_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String phoneNumber = rs.getString("phone_number");
				int locationId = rs.getInt("location_id");
				String dobStr = rs.getString("date_of_birth");
				LocalDate dob = LocalDate.parse(dobStr);
				Location location = locationDAO.getLocation(locationId);
				Patient patient = new Patient(patientId,firstName,lastName,phoneNumber,location,dob);
				return patient;
			}
			
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			
		}return null;
	}
	
	public ArrayList<Patient> getAllPatients(){
		ArrayList<Patient> allPatients = new ArrayList<>();
		String queryForAllPatients = "SELECT * FROM patient";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement allPatientPST = con.prepareStatement(queryForAllPatients)){
			ResultSet rs = allPatientPST.executeQuery();
			while (rs.next()) {
				String patientId = rs.getString("patient_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String phoneNumber = rs.getString("phone_number");
				int locationId = rs.getInt("location_id");
				Location location = locationDAO.getLocation(locationId);
				String dobStr = rs.getString("date_of_birth");
				LocalDate dob = LocalDate.parse(dobStr);
				Patient patient = new Patient(patientId,firstName,lastName,phoneNumber,location,dob);
				ArrayList<Symptom>patientSymptoms = getPatientSymptom(patientId);
				patient.setMedicalSymptoms(patientSymptoms);
				allPatients.add(patient);
			}
			
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}return allPatients;
	}
	
	private ArrayList<Symptom> getPatientSymptom(String id){
		ArrayList<Symptom> allPatientSymptoms = new ArrayList<>();
		String queryToGetSymptomForPatient = "SELECT s.symptom_id, s.name, s.priority " +
			    	    "FROM patient_symptoms ps " +
			    	    "JOIN symptoms s ON ps.symptom_id = s.symptom_id " +
			    	    "WHERE ps.patient_id = ?";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement getPatientSymptomPST = con.prepareStatement(queryToGetSymptomForPatient)){
			getPatientSymptomPST.setString(1, id);
			ResultSet rs = getPatientSymptomPST.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String priorityStr = rs.getString("priority");
				SymptomPriority priority = SymptomPriority.valueOf(priorityStr);
				Symptom symptom = new Symptom(name,priority);
				allPatientSymptoms.add(symptom);
			}
			
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}return allPatientSymptoms;
	}
}
