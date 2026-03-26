

package com.joshua.triage;

import com.joshua.location.LocationService;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseAvailability;
import com.joshua.patient.Patient;
import com.joshua.location.Location;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomPriority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class HeuristicCostFunctionTest {

    private TriageService triageService;
    private Nurse nurse;
    private Patient urgentPatient;
    private Patient nonUrgentPatient;

    @BeforeEach
    void setUp() {

        LocationService mockLocationService = Mockito.mock(LocationService.class);
       
        Mockito.when(mockLocationService.calculateDistance(any(Location.class), any(Location.class)))
               .thenReturn(10.0);

        triageService = new TriageService(mockLocationService);

        Location nurseLocation = new Location(52.52, 13.405);
        nurse = new Nurse("N001", "John", "Doe", "1234567890",
                nurseLocation, NurseAvailability.AVAILABLE, 5);

        // Urgent patient with symptom priority EMERGENCY (mapped to 5)
        ArrayList<Symptom> urgentSymptoms = new ArrayList<>();
        urgentSymptoms.add(new Symptom("Chest Pain", SymptomPriority.EMERGENCY));
        urgentPatient = new Patient("P001", "Alice", "Brown", "0000000000",
                nurseLocation, LocalDate.of(1990, 1, 1),
                urgentSymptoms, 0);

        // Non-urgent patient with symptom priority NONURGENT (mapped to 1)
        ArrayList<Symptom> nonUrgentSymptoms = new ArrayList<>();
        nonUrgentSymptoms.add(new Symptom("Mild Headache", SymptomPriority.NONURGENT));
        nonUrgentPatient = new Patient("P002", "Charlie", "Miller", "1111111111",
                nurseLocation, LocalDate.of(1985, 5, 20),
                nonUrgentSymptoms, 0);
    }

    @Test
    void testUrgentPatientGetsLowerScore() {
        double urgentScore = triageService.calculateCost(nurse, urgentPatient);
        double nonUrgentScore = triageService.calculateCost(nurse, nonUrgentPatient);

        System.out.println("Urgent score = " + urgentScore);
        System.out.println("Non-urgent score = " + nonUrgentScore);

        assertTrue(urgentScore < nonUrgentScore,
                "Urgent patient should have lower score than non-urgent patient with same nurse and distance");
    }
    
    @Test
    void testExperiencedNursePreferredForUrgentPatient() {

        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom("Severe Bleeding", SymptomPriority.EMERGENCY));
        Patient urgentPatient = new Patient("P100", "Test", "Patient", "0000000000",
                nurse.getLocation(), LocalDate.of(1995, 3, 15),
                symptoms, 0);

      
        Nurse experiencedNurse = new Nurse("N100", "Jane", "Doe", "1111111111",
                nurse.getLocation(), NurseAvailability.AVAILABLE, 10); // high exp
        Nurse juniorNurse = new Nurse("N200", "Jake", "Smith", "2222222222",
                nurse.getLocation(), NurseAvailability.AVAILABLE, 1); // low exp

  
        double experiencedScore = triageService.calculateCost(experiencedNurse, urgentPatient);
        double juniorScore = triageService.calculateCost(juniorNurse, urgentPatient);

        System.out.println("Experienced nurse score = " + experiencedScore);
        System.out.println("Junior nurse score = " + juniorScore);


        assertTrue(experiencedScore < juniorScore,
                "Experienced nurse should be preferred for urgent patients (lower score).");
    }

}
