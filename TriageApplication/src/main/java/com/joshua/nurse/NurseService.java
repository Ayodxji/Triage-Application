package com.joshua.nurse;

import java.util.Scanner;

import com.joshua.location.Location;
import com.joshua.location.LocationService;

public class NurseService {
	private LocationService locationService;
	
	public NurseService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public Nurse getNurseData() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please Enter Nurse ID");
		String nurseId  = scanner.nextLine();
		System.out.println("Please Enter First Name");
		String firstName = scanner.nextLine();
		System.out.println("Please Enter LastName");
		String lastName = scanner.nextLine();
		System.out.println("Enter Phone Number");
		String phoneNumber = scanner.nextLine();
		System.out.println("Enter number of Experience");
		String yearsOfExperienceStr = scanner.nextLine();
		int yearsOfExperience = Integer.parseInt(yearsOfExperienceStr);
		System.out.println("""
				AVAILABLE or
				UNAVAILABLE
				""");
		String availabilityStr = scanner.nextLine();
		NurseAvailability availability = NurseAvailability.valueOf(availabilityStr);
		System.out.println("Enter Location");
		Location nurseLocation = locationService.getLocationData();
		Nurse nurse = new Nurse(nurseId,firstName,lastName,phoneNumber,nurseLocation,availability,yearsOfExperience); 
		return nurse;
		
	}
}
