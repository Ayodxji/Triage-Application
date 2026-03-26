package com.joshua.api;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshua.location.Location;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;
import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomDAO;
import com.joshua.symptom.SymptomPriority;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientDAO patientDAO;
    private final SymptomDAO symptomDAO;

    public PatientController(PatientDAO patientDAO, SymptomDAO symptomDAO) {
        this.patientDAO = patientDAO;
        this.symptomDAO = symptomDAO;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<ArrayList<Patient>>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.ok(patientDAO.getAllPatients()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Patient>> getPatient(@PathVariable String id) {
        Patient patient = patientDAO.findPatient(id);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Patient not found: " + id));
        }
        return ResponseEntity.ok(ApiResponse.ok(patient));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<String>> addPatient(@RequestBody PatientRequest request) {
        Patient patient = request.toPatient();
        if (patientDAO.addPatient(patient, patient.getLocation())) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Patient created", patient.getPatientId()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create patient"));
    }


    @PostMapping("/{id}/symptoms")
    public ResponseEntity<ApiResponse<String>> assignSymptoms(
            @PathVariable String id,
            @RequestBody SymptomAssignRequest request) {
        Patient patient = patientDAO.findPatient(id);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Patient not found: " + id));
        }
        ArrayList<Symptom> symptoms = new ArrayList<>();
        for (String[] nameAndPriority : request.symptoms) {
            String name = nameAndPriority[0];
            SymptomPriority priority = SymptomPriority.valueOf(nameAndPriority[1]);
            symptoms.add(new Symptom(name, priority));
        }
        patient.setMedicalSymptoms(symptoms);
        if (patientDAO.assignSymptomsToPatient(patient)) {
            return ResponseEntity.ok(ApiResponse.ok("Symptoms assigned", id));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to assign symptoms"));
    }



    public static class PatientRequest {
        public String patientId;
        public String firstName;
        public String lastName;
        public String phoneNumber;
        public double latitude;
        public double longitude;
        public String dateOfBirth;

        public Patient toPatient() {
            Location location = new Location(latitude, longitude);
            return new Patient(patientId, firstName, lastName, phoneNumber, location,
                    LocalDate.parse(dateOfBirth));
        }
    }

    public static class SymptomAssignRequest {
        public ArrayList<String[]> symptoms;
    }
}
