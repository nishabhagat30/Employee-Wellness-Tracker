package com.example.EmployeeWellnessTracker.dto;

import java.util.List;

import com.example.EmployeeWellnessTracker.models.Questions;

public class SurveyUpdateRequest {
    private String templateName;
    private String category;
    private List<Questions> questions; 
    public String getTemplateName() {
        return templateName;
    }

    public String getCategory() {
        return category;
    }

    public List<Questions> getQuestions() {
        return questions;
    }
}