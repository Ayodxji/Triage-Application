package com.joshua.location;

import java.util.Scanner;

public class LocationService {
	public double calculateDistance(Location location1,Location location2) {
		final double earthRadius = 6371;
		double latitude1 = Math.toRadians(location1.getLatitude());
		double longitude1 = Math.toRadians(location1.getLongitude());
		double latitude2 = Math.toRadians(location2.getLatitude());
		double longitude2 = Math.toRadians(location2.getLongitude());
		
		double latitudeDifference = latitude2-latitude1;
		double longitudeDifference = longitude2-longitude1;
		
		double differenceBetweenPoints = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
		
		double angularDistanceBetweenPoints = 2 * Math.atan2(Math.sqrt(differenceBetweenPoints), Math.sqrt(1 - differenceBetweenPoints));
		
		return earthRadius*angularDistanceBetweenPoints;
	}
	
	public Location getLocationData() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Latitude");
		String latitudeStr = scanner.nextLine();
		System.out.println("Enter Longitude");
		String longitudeStr = scanner.nextLine();
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		Location location = new Location(latitude,longitude);
		return location;
	}
}
