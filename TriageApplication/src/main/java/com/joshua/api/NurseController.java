package com.joshua.api;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshua.location.Location;
import com.joshua.nurse.Nurse;
import com.joshua.nurse.NurseDAO;

@RestController
@RequestMapping("/api/nurses")
@CrossOrigin(origins = "*")
public class NurseController {

    private final NurseDAO nurseDAO;

    public NurseController(NurseDAO nurseDAO) {
        this.nurseDAO = nurseDAO;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ArrayList<Nurse>>> getAllNurses() {
        return ResponseEntity.ok(ApiResponse.ok(nurseDAO.getAllNurses()));
    }


    @GetMapping("/available")
    public ResponseEntity<ApiResponse<ArrayList<Nurse>>> getAvailableNurses() {
        ArrayList<Nurse> nurses = nurseDAO.getAllAvailableNurse();
        return ResponseEntity.ok(ApiResponse.ok(nurses));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<String>> addNurse(@RequestBody NurseRequest request) {
        Nurse nurse = request.toNurse();
        Location location = nurse.getLocation();
        if (nurseDAO.addNurse(nurse, location)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Nurse created", nurse.getNurseId()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to create nurse"));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateNurse(
            @PathVariable String id,
            @RequestBody NurseRequest request) {
        Nurse nurse = request.toNurse();
        if (!nurse.getNurseId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Path ID and body ID do not match"));
        }
        if (nurseDAO.updateNurse(nurse, nurse.getLocation())) {
            return ResponseEntity.ok(ApiResponse.ok("Nurse updated", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Nurse not found or update failed"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNurse(@PathVariable String id) {
        if (nurseDAO.deleteNurseById(id)) {
            return ResponseEntity.ok(ApiResponse.ok("Nurse deleted", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Nurse not found"));
    }



    public static class NurseRequest {
        public String nurseId;
        public String firstName;
        public String lastName;
        public String phoneNumber;
        public double latitude;
        public double longitude;
        public String availability;   // "AVAILABLE" or "UNAVAILABLE"
        public int yearsOfExperience;

        public Nurse toNurse() {
            Location location = new Location(latitude, longitude);
            com.joshua.nurse.NurseAvailability avail =
                com.joshua.nurse.NurseAvailability.valueOf(availability);
            return new Nurse(nurseId, firstName, lastName, phoneNumber, location, avail, yearsOfExperience);
        }
    }
}
