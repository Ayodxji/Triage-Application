package com.joshua.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshua.assignment.AssignmentDAO;
import com.joshua.assignment.AssignmentService;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseDAO;
import com.joshua.patient.Patient;
import com.joshua.patient.PatientDAO;
import com.joshua.triage.TriageService;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentDAO assignmentDAO;
    private final NurseDAO nurseDAO;
    private final PatientDAO patientDAO;
    private final TriageService triageService;

    public AssignmentController(AssignmentService assignmentService,
                                AssignmentDAO assignmentDAO,
                                NurseDAO nurseDAO,
                                PatientDAO patientDAO,
                                TriageService triageService) {
        this.assignmentService = assignmentService;
        this.assignmentDAO = assignmentDAO;
        this.nurseDAO = nurseDAO;
        this.patientDAO = patientDAO;
        this.triageService = triageService;
    }


    @PostMapping("/run")
    public ResponseEntity<ApiResponse<Map<String, ArrayList<String>>>> runAssignment() {
        ArrayList<Nurse> availableNurses = nurseDAO.getAllAvailableNurse();
        ArrayList<Patient> patients = patientDAO.getAllPatients();

        if (availableNurses.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No available nurses found"));
        }
        if (patients.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No patients found"));
        }

        for (Patient p : patients) {
            p.setTriageScore(triageService.calculateTriageLevel(p));
        }

        Map<Nurse, ArrayList<Patient>> assignmentMap =
            assignmentService.assignNurseToPatient(availableNurses, patients);

        assignmentDAO.saveAssignments(assignmentMap);

        // Convert to a JSON-friendly Map<nurseId, List<patientId>>
        Map<String, ArrayList<String>> result = new HashMap<>();
        for (Map.Entry<Nurse, ArrayList<Patient>> entry : assignmentMap.entrySet()) {
            ArrayList<String> patientIds = new ArrayList<>();
            for (Patient p : entry.getValue()) {
                patientIds.add(p.getPatientId());
            }
            result.put(entry.getKey().getNurseId(), patientIds);
        }

        return ResponseEntity.ok(ApiResponse.ok("Assignment complete", result));
    }

    /**
     * GET /api/assignments
     * Returns all stored assignments from the database.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, ArrayList<String>>>> getAssignments() {
        Map<String, ArrayList<String>> assignments = assignmentDAO.getAllAssignments();
        return ResponseEntity.ok(ApiResponse.ok(assignments));
    }
}
