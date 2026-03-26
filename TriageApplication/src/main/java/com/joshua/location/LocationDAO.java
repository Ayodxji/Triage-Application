package com.joshua.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.joshua.databaseconnection.DatabaseConnection;


public class LocationDAO {
	public int addLocation(Location location) {
		int locationId = isLocationPresent(location);
		String queryToAddLocation = "INSERT INTO location (latitude, longitude) VALUES (?, ?)";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement addLocationPST = con.prepareStatement(queryToAddLocation,1);){
			if (locationId>0) {
				return locationId;
			}else {
				addLocationPST.setDouble(1, location.getLatitude());
				addLocationPST.setDouble(2, location.getLongitude());
				addLocationPST.executeUpdate();
				ResultSet generatedKeys = addLocationPST.getGeneratedKeys();
				if (generatedKeys.next()) {
					locationId = generatedKeys.getInt(1);
					return locationId;
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}return locationId;
	}
	
	private int isLocationPresent(Location location) {
		int locationId = -1;
		// Use a small tolerance instead of exact equality — floating-point doubles
		// stored and retrieved from MySQL may differ by tiny amounts, causing the
		// same physical location to be inserted as multiple rows.
		String queryToFindExistingLocation =
			"SELECT location_id FROM location WHERE ABS(latitude - ?) < 0.000001 AND ABS(longitude - ?) < 0.000001";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement findLocationPST = con.prepareStatement(queryToFindExistingLocation, 1)){
			findLocationPST.setDouble(1,location.getLatitude());
			findLocationPST.setDouble(2, location.getLongitude());
			ResultSet rs = findLocationPST.executeQuery();
			if (rs.next()) {
				locationId = rs.getInt("location_id");
				return locationId;
			}
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}return locationId;
	}
	
	public Location getLocation(int id) {
		String queryTogetLocation = "SELECT * FROM location WHERE location_id = ?";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement getLocationPST = con.prepareStatement(queryTogetLocation)){
			getLocationPST.setInt(1, id);
			ResultSet locationSet = getLocationPST.executeQuery();
			if (locationSet.next()) {
				double latitude = locationSet.getDouble("latitude");
				double longitude = locationSet.getDouble("longitude");
				Location location = new Location(latitude,longitude);
				return location;
			}
			
		}catch(SQLException e) {
			System.out.println("Location Not Found");
		}return null;
		
		
	}
	
//	public boolean updateLocation(Location location) {
//		String queryToUpdateLocation = "UPDATE location SET latitude = ?, longitude = ? WHERE id = ?";
//		try(Connection con = DatabaseConnection.getDBConnection();
//				PreparedStatement updateLocationPST = con.prepareStatement(queryToUpdateLocation)){
//			
//			
//		}catch() {
//			
//		}
//		
//
//	}
}
