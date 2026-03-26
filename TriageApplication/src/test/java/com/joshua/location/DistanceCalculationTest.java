package com.joshua.location;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.joshua.location.Location;
import com.joshua.triage.TriageService;

public class DistanceCalculationTest {

    private LocationService locationService = new LocationService();

    @Test
    public void testSameLocation() {
        Location loc1 = new Location(51.5074, -0.1278);
        Location loc2 = new Location(51.5074, -0.1278);
        double distance = locationService.calculateDistance(loc1, loc2);
        assertEquals(0.0, distance, 0.001, "Distance between identical locations should be zero");
    }

    @Test
    public void testKnownDistance() {
        Location testLocation1 = new Location(51.5074, -0.1278);
        Location testLocaion2 = new Location(48.8566, 2.3522);
        double distance = locationService.calculateDistance(testLocation1, testLocaion2);
        assertTrue(distance > 340 && distance < 350, "Distance should be approx 343 km");
    }
}
