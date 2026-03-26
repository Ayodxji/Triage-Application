//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//import com.joshua.location.LocationService;
//import com.joshua.nurse.Nurse;
//import com.joshua.patient.Patient;
//import com.joshua.symptom.Symptom;
//import com.joshua.symptom.SymptomPriority;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//
//class TriageServiceTest {
//
//    private LocationService locationService;
//    private TriageService triageService;
//
//    @BeforeEach
//    void setUp() {
//        locationService = mock(LocationService.class);
//        triageService = new TriageService(locationService);
//    }
//
//    @Test
//    void testCalculateTriageLevel_WithMultipleSymptoms() {
//        Patient patient = mock(Patient.class);
//        Symptom symptom1 = mock(Symptom.class);
//        Symptom symptom2 = mock(Symptom.class);
//
//        SymptomPriority lowPriority = mock(SymptomPriority.class);
//        SymptomPriority highPriority = mock(SymptomPriority.class);
//
//        when(lowPriority.getUrgencyValue()).thenReturn(2);
//        when(highPriority.getUrgencyValue()).thenReturn(5);
//
//        when(symptom1.getSymptomPriority()).thenReturn(lowPriority);
//        when(symptom2.getSymptomPriority()).thenReturn(highPriority);
//        when(patient.getMedicalSymptoms()).thenReturn(Arrays.asList(symptom1, symptom2));
//
//        int triageLevel = triageService.calculateTriageLevel(patient);
//
//        assertEquals(5, triageLevel);
//    }
//
//    @Test
//    void testCalculateCost_WithMocks() {
//        Nurse nurse = mock(Nurse.class);
//        Patient patient = mock(Patient.class);
//        Symptom symptom = mock(Symptom.class);
//        SymptomPriority priority = mock(SymptomPriority.class);
//
//        
//        when(nurse.getLocation()).thenReturn("LocationA");
//        when(nurse.getYearsOfExperience()).thenReturn(8);
//
//        
//        when(patient.getLocation()).thenReturn("LocationB");
//        when(priority.getUrgencyValue()).thenReturn(4);
//        when(symptom.getSymptomPriority()).thenReturn(priority);
//        when(patient.getMedicalSymptoms()).thenReturn(Arrays.asList(symptom));
//
//        
//        when(locationService.calculateDistance("LocationA", "LocationB")).thenReturn(20.0);
//
//        
//        double distance = 20.0;
//        double fuelUsed = distance * Nurse.FUEL_CONSUMPTION;
//        double fuelCost = fuelUsed * 1.6;
//        int urgency = 4;
//        int experience = 8;
//
//        double expected = (3 * urgency) + (1.5 * fuelCost) - (0.5 * experience * urgency);
//
//        
//        double result = triageService.calculateCost(nurse, patient);
//
//        assertEquals(expected, result, 0.0001);
//    }
//}
