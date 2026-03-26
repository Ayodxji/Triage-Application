package com.joshua.symptom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.joshua.databaseconnection.DatabaseConnection;

public class SymptomDAO {
	public ArrayList<Symptom> getAllSymptoms(){
		ArrayList<Symptom> allSymptoms = new ArrayList();
		String queryForAllSymptoms = "SELECT symptom_id, name, priority FROM symptoms";
		try (Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement getSymptomsPST = con.prepareStatement(queryForAllSymptoms)){
			ResultSet symptomSet =  getSymptomsPST.executeQuery();
			while (symptomSet.next()) {
				String name = symptomSet.getString("name");
				String priority = symptomSet.getString("priority");
				Symptom symptom = new Symptom(name,SymptomPriority.valueOf(priority));
				allSymptoms.add(symptom);
			}
			return allSymptoms;
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return allSymptoms;
	}
	
	public int getSymptomIdFromName(String symptomName) throws SQLException {
		String queryToRetrieveSymptomName = "SELECT symptom_id FROM symptoms WHERE name = ?";
		try(Connection con = DatabaseConnection.getDBConnection();
				PreparedStatement getSymptomNamePST = con.prepareStatement(queryToRetrieveSymptomName);){
			getSymptomNamePST.setString(1, symptomName);
			ResultSet rs = getSymptomNamePST.executeQuery();
			if (rs.next()) {
				return rs.getInt("symptom_id");
			}else {
				throw new SQLException("Symptom Not Found "+symptomName);
			}
		}
	}
}
