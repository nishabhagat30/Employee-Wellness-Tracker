package com.example.EmployeeWellnessTracker.dto;

import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.fasterxml.jackson.annotation.JsonProperty;


    public class ResponseAnswerDto {
        
    @JsonProperty("questionText")
    private String questionText;

    @JsonProperty("selectedOption")
    private String selectedOption;

    public ResponseAnswerDto(ResponseAnswer answer) {
        this.questionText = (answer.getQuestion() != null) ? answer.getQuestion().getQuestionText() : null;
        this.selectedOption = answer.getAnswerText();
    }
    
}
