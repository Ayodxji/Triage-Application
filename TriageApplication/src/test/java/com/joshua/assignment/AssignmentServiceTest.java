package com.joshua.assignment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.joshua.location.Location;
import com.joshua.location.LocationDAO;
import com.joshua.location.LocationService;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseAvailability;
import com.joshua.nurse.NurseDAO;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;
import com.joshua.symptom.SymptomDAO;
import com.joshua.triage.TriageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class AssignmentServiceTest {

    private AssignmentService assignmentService;
    private TriageService triageService;
    private PatientDAO patientDAO;
    private LocationDAO locationDAO;
    private SymptomDAO symptomDAO;
    private NurseDAO nurseDAO;
    private LocationService locationService;

    private Location nurseLocation;
    private Location patientLocation;

    private Nurse nurseA;
    private Nurse nurseB;
    private Nurse singleNurse;
    private Patient urgentPatient;
    private Patient normalPatient;

    @BeforeEach
    public void setUp() {
        nurseLocation = new Location(52.52, 13.405);
        patientLocation = new Location(48.8566, 2.3522);

        locationService = new LocationService();
        triageService = new TriageService(locationService);
        locationDAO = new LocationDAO();
        symptomDAO = new SymptomDAO();
        patientDAO = new PatientDAO(locationDAO,symptomDAO);
        nurseDAO = new NurseDAO(locationDAO);

        assignmentService = new AssignmentService(triageService, patientDAO, nurseDAO);

        // Create nurses
        nurseA = new Nurse("N001", "John", "Doe", "1234567890",
                nurseLocation, NurseAvailability.AVAILABLE, 10); 
        nurseB = new Nurse("N002", "Bob", "Jones", "1234567890",
                nurseLocation, NurseAvailability.AVAILABLE, 3);
        singleNurse = new Nurse("N003", "Mark", "Taylor", "1234567890",
                nurseLocation, NurseAvailability.AVAILABLE, 5);

        // Create patients
        urgentPatient = new Patient("P001", "Alice", "Brown", "0000000000",
                patientLocation, LocalDate.of(1995, 1, 1), new ArrayList<>(), 5); // urgent
        normalPatient = new Patient("P002", "Charlie", "Miller", "0000000000",
                patientLocation, LocalDate.of(1990, 1, 1), new ArrayList<>(), 1); // non-urgent
    }

    @Test
    public void testAssignSinglePatient() {
        ArrayList<Nurse> nurses = new ArrayList<>();
        nurses.add(nurseA);

        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(normalPatient);

        Map<Nurse, ArrayList<Patient>> result = assignmentService.assignNurseToPatient(nurses, patients);

        assertTrue(result.containsKey(nurseA), "Nurse A should be assigned");
        assertEquals(1, result.get(nurseA).size(), "Nurse A should have one patient");
    }

    @Test
    public void testUrgentPatientAssignedFirst() {
        ArrayList<Nurse> nurses = new ArrayList<>();
        nurses.add(nurseA);
        nurses.add(nurseB);

        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(urgentPatient);
        patients.add(normalPatient);

        Map<Nurse, ArrayList<Patient>> result = assignmentService.assignNurseToPatient(nurses, patients);

        boolean urgentAssigned = result.values().stream()
                .flatMap(ArrayList::stream)
                .anyMatch(p -> p.getPatientId().equals("P001"));

        assertTrue(urgentAssigned, "Urgent patient must always be assigned first");
    }

    @Test
    public void testNurseCapacityLimit() {
        ArrayList<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            patients.add(new Patient("P00" + i, "Patient" + i, "Last" + i, "000000000" + i,
                    patientLocation, LocalDate.of(1990, 1, 1), new ArrayList<>(), 1));
        }

        ArrayList<Nurse> nurses = new ArrayList<>();
        nurses.add(singleNurse);

        Map<Nurse, ArrayList<Patient>> assignmentMap = assignmentService.assignNurseToPatient(nurses, patients);

        assertEquals(5, assignmentMap.get(singleNurse).size(),
                "Nurse should only be assigned 5 patients (capacity limit)");
        assertEquals(1, patients.size() - assignmentMap.get(singleNurse).size(),
                "One patient should remain unassigned due to nurse capacity limit");
    }

    /**
     * Verifies that when nurse capacity forces one patient to be left unassigned,
     * it is the non-urgent patient that misses out — not an urgent one.
     * This test fails without the pre-sort fix in AssignmentService.
     */
    @Test
    public void testNonUrgentPatientLeftUnassignedWhenCapacityFull() {
        ArrayList<Nurse> nurses = new ArrayList<>();
        nurses.add(singleNurse); // capacity = 5

        // 5 EMERGENCY patients + 1 NONURGENT patient = 6 total; capacity = 5
        ArrayList<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            patients.add(new Patient("EMRG0" + i, "Emrg" + i, "Last" + i, "000000000" + i,
                    patientLocation, LocalDate.of(1990, 1, 1), new ArrayList<>(), 5)); // EMERGENCY score
        }
        Patient nonUrgent = new Patient("LOW01", "Low", "Priority", "0000000000",
                patientLocation, LocalDate.of(1990, 1, 1), new ArrayList<>(), 1); // NONURGENT score
        // Add non-urgent FIRST so that without sorting it would be assigned before the 5th emergency
        patients.add(0, nonUrgent);

        Map<Nurse, ArrayList<Patient>> result = assignmentService.assignNurseToPatient(nurses, patients);

        ArrayList<Patient> assigned = result.get(singleNurse);
        assertEquals(5, assigned.size(), "Nurse should have exactly 5 patients assigned");

        boolean nonUrgentAssigned = assigned.stream()
                .anyMatch(p -> p.getPatientId().equals("LOW01"));
        assertFalse(nonUrgentAssigned,
                "The non-urgent patient should be the one left unassigned when capacity is full");
    }

    @Test
    public void testNoAvailableNurse() {
        ArrayList<Nurse> nurses = new ArrayList<>();
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(normalPatient);

        Map<Nurse, ArrayList<Patient>> result = assignmentService.assignNurseToPatient(nurses, patients);

        assertTrue(result.isEmpty(), "No assignment should be made if no nurses are available");
    }
}
