package com.joshua.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.joshua.assignment.AssignmentDAO;
import com.joshua.assignment.AssignmentService;
import com.joshua.location.LocationDAO;
import com.joshua.location.LocationService;
import com.joshua.nurse.NurseDAO;
import com.joshua.patient.PatientDAO;
import com.joshua.symptom.SymptomDAO;
import com.joshua.triage.TriageService;

@SpringBootApplication
@ComponentScan(basePackages = {"com.joshua.api"})
public class TriageApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TriageApiApplication.class, args);
    }



    @Bean
    public LocationService locationService() {
        return new LocationService();
    }

    @Bean
    public LocationDAO locationDAO() {
        return new LocationDAO();
    }

    @Bean
    public SymptomDAO symptomDAO() {
        return new SymptomDAO();
    }

    @Bean
    public NurseDAO nurseDAO(LocationDAO locationDAO) {
        return new NurseDAO(locationDAO);
    }

    @Bean
    public PatientDAO patientDAO(LocationDAO locationDAO, SymptomDAO symptomDAO) {
        return new PatientDAO(locationDAO, symptomDAO);
    }

    @Bean
    public TriageService triageService(LocationService locationService) {
        return new TriageService(locationService);
    }

    @Bean
    public AssignmentService assignmentService(TriageService triageService,
                                               PatientDAO patientDAO,
                                               NurseDAO nurseDAO) {
        return new AssignmentService(triageService, patientDAO, nurseDAO);
    }

    @Bean
    public AssignmentDAO assignmentDAO(NurseDAO nurseDAO, PatientDAO patientDAO) {
        return new AssignmentDAO(nurseDAO, patientDAO);
    }
}
