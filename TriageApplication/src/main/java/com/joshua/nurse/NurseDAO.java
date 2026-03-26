package com.joshua.nurse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.joshua.databaseconnection.DatabaseConnection;
import com.joshua.location.Location;
import com.joshua.location.LocationDAO;

public class NurseDAO {
	private LocationDAO locationDAO;
	
	public NurseDAO(LocationDAO locationDAO) {
		this.locationDAO = locationDAO;
	}
	public boolean addNurse(Nurse nurse,Location location) {
		String addNurseQuery = "INSERT INTO nurse (nurse_id, first_name, last_name, phone_number, location_id, experience, availability)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
		// Both inserts run inside a single transaction so a failed nurse insert
		// cannot leave an orphaned location row.
		try(Connection con = DatabaseConnection.getDBConnection()){
			con.setAutoCommit(false);
			int locationId = locationDAO.addLocation(location);
			if (locationId < 0) {
				con.rollback();
				System.out.println("Failed to Insert Location");
				return false;
			}
			try(PreparedStatement addNursePST = con.prepareStatement(addNurseQuery)){
				addNursePST.setString(1, nurse.getNurseId());
				addNursePST.setString(2, nurse.getFirstName());
				addNursePST.setString(3, nurse.getLastName());
				addNursePST.setString(4, nurse.getPhoneNumber());
				addNursePST.setInt(5, locationId);
				addNursePST.setInt(6, nurse.getYearsOfExperience());
				addNursePST.setString(7, nurse.getNurseAvailability().name());
				int rowsAffected = addNursePST.executeUpdate();
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
	
	public ArrayList<Nurse> getAllNurses(){
		ArrayList<Nurse> allNurses = new ArrayList<>();
		String query = "SELECT * FROM nurse";
		try (Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement pst = con.prepareStatement(query)){
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String nurseId = rs.getString("nurse_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String phoneNumber = rs.getString("phone_number");
				int locationId = rs.getInt("location_id");
				int experience = rs.getInt("experience");
				NurseAvailability availability = NurseAvailability.valueOf(rs.getString("availability"));
				Location location = locationDAO.getLocation(locationId);
				allNurses.add(new Nurse(nurseId,firstName,lastName,phoneNumber,location,availability,experience));
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}return allNurses;
	}

	public ArrayList<Nurse> getAllAvailableNurse(){
		ArrayList<Nurse> availableNurse = new ArrayList<>();
		String queryToFetchAvailableNurses = "SELECT * FROM nurse WHERE availability = 'AVAILABLE'";
		try (Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement fetchNursePST = con.prepareStatement(queryToFetchAvailableNurses)){
			ResultSet rs = fetchNursePST.executeQuery();
			while (rs.next()) {
				String nurseId = rs.getString("nurse_id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String phoneNumber = rs.getString("phone_number");
				int locationId = rs.getInt("location_id");
				int experience = rs.getInt("experience");
				NurseAvailability availability = NurseAvailability.valueOf(rs.getString("availability"));
				Location location = locationDAO.getLocation(locationId);
				Nurse nurse = new Nurse(nurseId,firstName,lastName,phoneNumber,location,availability,experience);
				availableNurse.add(nurse);
			}return availableNurse;
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}return availableNurse;
	}
	
	public boolean deleteNurseById(String nurseId) {
	    String deleteQuery = "DELETE FROM nurse WHERE nurse_id = ?";

	    try (Connection con = DatabaseConnection.getDBConnection();
	         PreparedStatement pst = con.prepareStatement(deleteQuery)) {

	        pst.setString(1, nurseId);
	        int rowsAffected = pst.executeUpdate();
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return false;
	}
	
	public boolean updateNurse(Nurse nurse, Location location) {
	    int locationId = locationDAO.addLocation(location);
	    if (locationId < 0) {
	        System.out.println("Failed to update or insert location");
	        return false;
	    }

	    String updateQuery = "UPDATE nurse SET first_name = ?, last_name = ?, phone_number = ?, "
	                       + "location_id = ?, experience = ?, availability = ? "
	                       + "WHERE nurse_id = ?";

	    try (Connection con = DatabaseConnection.getDBConnection();
	         PreparedStatement pst = con.prepareStatement(updateQuery)) {

	        pst.setString(1, nurse.getFirstName());
	        pst.setString(2, nurse.getLastName());
	        pst.setString(3, nurse.getPhoneNumber());
	        pst.setInt(4, locationId);
	        pst.setInt(5, nurse.getYearsOfExperience());
	        pst.setString(6, nurse.getNurseAvailability().name());
	        pst.setString(7, nurse.getNurseId());

	        int rowsAffected = pst.executeUpdate();
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return false;
	}


}
