package com.example.EmployeeWellnessTracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.dto.SurveyResponseDto;
import com.example.EmployeeWellnessTracker.dto.SurveyResponseRequest;
import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.example.EmployeeWellnessTracker.models.SurveyResponse;
import com.example.EmployeeWellnessTracker.services.SurveyResponseService;



@RestController
@RequestMapping("/survey-responses")
public class SurveyResponseController {

    @Autowired
   private SurveyResponseService surveyResponseService;


// Submit Survey Response (User)
@PostMapping("/submit")
public ResponseEntity<String> submitSurveyResponse(@RequestBody SurveyResponseRequest request) {
    String result = surveyResponseService.submitSurveyResponse(request);
    if (result.equals("Survey response submitted successfully.")) {
        return ResponseEntity.ok(result);
    } else {
        return ResponseEntity.badRequest().body(result);
    }
}

      //Get Survey Responses by Response ID (User)
      @GetMapping("/{responseId}")
      public ResponseEntity<?> getSurveyResponseById(@PathVariable Long responseId, Principal principal) {
      SurveyResponse response = surveyResponseService.getSurveyResponseById(responseId, principal.getName());
      return ResponseEntity.ok(response);
  }


// Get Past Survey Responses (For Logged in User)
  @GetMapping("/user-submissions")
  @PreAuthorize("isAuthenticated()") 
  public ResponseEntity<List<SurveyResponseDto>> getUserSubmissions() {
      List<SurveyResponseDto> submissions = surveyResponseService.getUserSubmissions();
      return ResponseEntity.ok(submissions);
  }

    // Delete a survey response by Response ID (User)
    @DeleteMapping("/delete/{responseId}")
    public ResponseEntity<String> deleteSurveyResponse(@PathVariable Long responseId) {
        try {
            surveyResponseService.deleteSurveyResponse(responseId);
            return ResponseEntity.ok("Survey response deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error deleting survey response: " + e.getMessage());
        }
    }

  
   // Update Survey response by Response ID (User)
    @PutMapping("/{responseId}")
    public ResponseEntity<?> updateSurveyResponse(@PathVariable Long responseId, @RequestBody SurveyResponse request) {
        try {
            List<ResponseAnswer> updatedAnswers = request.getAnswers();
            SurveyResponse updatedResponse = surveyResponseService.updateSurveyResponse(responseId, updatedAnswers);
            return ResponseEntity.ok(updatedResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating survey response: " + e.getMessage());
        }
    }



}
