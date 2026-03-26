package com.joshua.api;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joshua.symptom.Symptom;
import com.joshua.symptom.SymptomDAO;

@RestController
@RequestMapping("/api/symptoms")
@CrossOrigin(origins = "*")
public class SymptomController {

    private final SymptomDAO symptomDAO;

    public SymptomController(SymptomDAO symptomDAO) {
        this.symptomDAO = symptomDAO;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<ArrayList<Symptom>>> getAllSymptoms() {
        return ResponseEntity.ok(ApiResponse.ok(symptomDAO.getAllSymptoms()));
    }
}
