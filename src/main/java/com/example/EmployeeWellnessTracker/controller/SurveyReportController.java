package com.example.EmployeeWellnessTracker.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.services.SurveyReportService;


@RestController
@RequestMapping("/reports")
public class SurveyReportController {


    @Autowired
    private SurveyReportService surveyReportService ;

    // Generate wellness report
    @GetMapping("/wellness")
    public ResponseEntity<?> getWellnessReport(
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime endDateTime) {
        Map<String, Map<String, Map<String, Integer>>> report = surveyReportService.getWellnessReport(startDateTime, endDateTime);
        return ResponseEntity.ok(report);
    }

}


