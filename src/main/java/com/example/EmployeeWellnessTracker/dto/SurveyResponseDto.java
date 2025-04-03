package com.example.EmployeeWellnessTracker.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.EmployeeWellnessTracker.models.SurveyResponse;

public class SurveyResponseDto {
    private Long responseId;
    private String userName;
    private String userEmail;
    private Long templateId;
    private String templateName;
    private String category;
    private LocalDateTime submittedAt;
    private LocalDateTime editableUntil;
    private List<ResponseAnswerDto> answers;

    public SurveyResponseDto(SurveyResponse response) {
        this.responseId = response.getResponseId();
        this.userName = response.getUser().getName();
        this.userEmail = response.getUser().getEmail();
        this.templateId = response.getSurveyTemplate().getTemplateId();
        this.templateName = response.getSurveyTemplate().getTemplateName();
        this.category = response.getSurveyTemplate().getCategory();
        this.submittedAt = response.getSubmittedAt();
        this.editableUntil = response.getEditableUntil();

        // Convert answers to DTO
        this.answers = response.getAnswers().stream()
                .map(ResponseAnswerDto::new)
                .collect(Collectors.toList());
    }

    public SurveyResponseDto(Long templateId) {
        this.templateId = templateId;
    }

    // Getters and Setters

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getEditableUntil() {
        return editableUntil;
    }

    public void setEditableUntil(LocalDateTime editableUntil) {
        this.editableUntil = editableUntil;
    }

    public List<ResponseAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ResponseAnswerDto> answers) {
        this.answers = answers;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }


}
