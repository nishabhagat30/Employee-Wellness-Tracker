package com.example.EmployeeWellnessTracker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.models.SurveyTemplates;
import com.example.EmployeeWellnessTracker.services.SurveyTemplateService;

@RestController
@RequestMapping("/survey-templates")
public class SurveyTemplateController {

   

    @Autowired
    private SurveyTemplateService surveyTemplateService;

   //Create Template (Admin)
    @PostMapping
    public ResponseEntity<?> createTemplate(@RequestBody SurveyTemplates surveyTemplate) {
        SurveyTemplates createdTemplate = surveyTemplateService.createSurveyTemplate(surveyTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }


   //  Update Survey Template(Admin)
    @PutMapping("/{templateid}")
    public ResponseEntity<?> updateSurvey(@PathVariable Long templateid, @RequestBody SurveyTemplates surveyTemplate) {
        SurveyTemplates template = surveyTemplateService.updateSurveyTemplate(templateid, surveyTemplate);
        return ResponseEntity.ok(template);
    }

    //Delete Survey template by id  (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSurveyTemplate(@PathVariable Long id) {
        return surveyTemplateService.deleteSurveyTemplate(id);
    }

    //Delete question by id (Admin)
    @DeleteMapping("/question/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        return surveyTemplateService.deleteQuestion(id);
    }

    //Get all survey   (Admin/Employee)
    @GetMapping("/available")
    public ResponseEntity<List<SurveyTemplates>> getAvailableSurveyTemplates() {
        List<SurveyTemplates> surveyTemplates = surveyTemplateService.getAvailableSurveyTemplates();
        return ResponseEntity.ok(surveyTemplates);  // Return the survey templates
    }

   // Get Survey Template by ID (Admin/Employee)
    @GetMapping("/{id}")
    public ResponseEntity<SurveyTemplates> getSurveyById(@PathVariable Long id) {
        Optional<SurveyTemplates> survey = surveyTemplateService.getSurveyById(id);
        return survey.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}


