package com.joshua.nurse;

import com.joshua.location.Location;
import com.joshua.user.User;

public class Nurse extends User{
	private String nurseId;
	private NurseAvailability availability;
	private int yearsOfExperience;
	public static final double FUEL_CONSUMPTION = 0.065;
	
	public Nurse(String nurseId,String firstName,String lastName,String phoneNumber,Location location,NurseAvailability availability,int yearsOfExperience) {
		super(firstName,lastName,phoneNumber,location);
		this.nurseId = nurseId;
		this.availability=availability;
		this.yearsOfExperience = yearsOfExperience;
	}
	
	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}
	
	public String getNurseId() {
		return nurseId;
	}
	
	public int getYearsOfExperience() {
		return yearsOfExperience;
	}
	
	public void setYearsOfExperience(int yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}
	
	public NurseAvailability getNurseAvailability() {
		return availability;
	}
	public void setNurseAvailability(NurseAvailability availability) {
		this.availability = availability;
	}
}
