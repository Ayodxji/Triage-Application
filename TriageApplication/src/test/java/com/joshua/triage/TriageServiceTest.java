package com.joshua.triage;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.joshua.location.Location;
import com.joshua.patient.Patient;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomPriority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class TriageServiceTest {

    private TriageService triageService;

    @BeforeEach
    public void setUp() {
        triageService = new TriageService();
    }


    private Patient createPatient(String id, String firstName, String lastName,
                                  ArrayList<Symptom> symptoms) {
        String phone = "0000000000";
        Location loc = null;
        LocalDate dob = LocalDate.of(1990, 1, 1);
        int triageScore = 0;
        return new Patient(id, firstName, lastName, phone, loc, dob, symptoms, triageScore);
    }

    @Test
    public void testSingleSymptom() {
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom("Chest Pain", SymptomPriority.EMERGENCY));

        Patient patient = createPatient("P001", "John", "Doe", symptoms);

        int urgency = triageService.calculateTriageLevel(patient);
        assertEquals(SymptomPriority.EMERGENCY.getUrgencyValue(), urgency,
            "Urgency should equal EMERGENCY priority value");
    }

    @Test
    public void testMultipleSymptomsReturnsMax() {
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom("High Fever", SymptomPriority.URGENT));   
        symptoms.add(new Symptom("Chest Pain", SymptomPriority.EMERGENCY));

        Patient patient = createPatient("P002", "Alice", "Smith", symptoms);

        int urgency = triageService.calculateTriageLevel(patient);
        assertEquals(SymptomPriority.EMERGENCY.getUrgencyValue(), urgency,
            "Urgency should reflect the highest-priority symptom (EMERGENCY)");
    }

    @Test
    public void testNoSymptomsReturnsDefaultNonUrgent() {
        // An empty symptom list should default to NONURGENT (1), not 0.
        // Returning 0 would silently deprioritise patients whose symptoms haven't
        // been recorded yet, causing them to be assigned last or not at all.
        ArrayList<Symptom> symptoms = new ArrayList<>();
        Patient patient = createPatient("PAT01", "Bob", "Lee", symptoms);

        int urgency = triageService.calculateTriageLevel(patient);
        assertEquals(SymptomPriority.NONURGENT.getUrgencyValue(), urgency,
            "Urgency should default to NONURGENT (1) when no symptoms are recorded");
    }
}
