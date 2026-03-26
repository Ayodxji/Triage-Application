package com.joshua.assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.joshua.databaseconnection.DatabaseConnection;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseDAO;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;

public class AssignmentDAO {
	private NurseDAO nurseDAO;
	private PatientDAO patientDAO;

	public AssignmentDAO(NurseDAO nurseDAO, PatientDAO patientDAO) {
		this.nurseDAO = nurseDAO;
		this.patientDAO = patientDAO;
	}

	public boolean saveAssignments(Map<Nurse, ArrayList<Patient>> assignmentMap) {
		String insertQuery = "INSERT INTO assignments (nurse_id, patient_id, assigned_at) VALUES (?, ?, ?)";
		try (Connection con = DatabaseConnection.getDBConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
				Timestamp now = Timestamp.valueOf(LocalDateTime.now());
				for (Map.Entry<Nurse, ArrayList<Patient>> entry : assignmentMap.entrySet()) {
					String nurseId = entry.getKey().getNurseId();
					for (Patient patient : entry.getValue()) {
						pst.setString(1, nurseId);
						pst.setString(2, patient.getPatientId());
						pst.setTimestamp(3, now);
						pst.addBatch();
					}
				}
				pst.executeBatch();
				con.commit();
				return true;
			} catch (SQLException e) {
				con.rollback();
				System.out.println("Failed to save assignments: " + e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Retrieves all stored assignments keyed by nurse_id.
	 * Returns a map of nurse_id -> list of patient_ids.
	 */
	public Map<String, ArrayList<String>> getAllAssignments() {
		Map<String, ArrayList<String>> results = new HashMap<>();
		String query = "SELECT nurse_id, patient_id, assigned_at FROM assignments ORDER BY assigned_at DESC";
		try (Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement pst = con.prepareStatement(query)) {
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String nurseId = rs.getString("nurse_id");
				String patientId = rs.getString("patient_id");
				results.computeIfAbsent(nurseId, k -> new ArrayList<>()).add(patientId);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return results;
	}
}
