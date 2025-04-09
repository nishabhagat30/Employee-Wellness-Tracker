package com.example.EmployeeWellnessTracker.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "survey_responses")
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SurveyTemplates surveyTemplate;

    @OneToMany(mappedBy = "surveyResponse", cascade = CascadeType.ALL)
   @JsonManagedReference // Manages forward serialization
   @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ResponseAnswer> answers;

    private LocalDateTime submittedAt;
    private LocalDateTime editableUntil;

    public SurveyResponse() {
        this.submittedAt = LocalDateTime.now();
        this.editableUntil = this.submittedAt.plusMinutes(30);
    }

    public SurveyResponse(User user, SurveyTemplates surveyTemplate) {
        this.user = user;
        this.surveyTemplate = surveyTemplate;
        this.submittedAt = LocalDateTime.now();
        this.editableUntil = this.submittedAt.plusMinutes(30);
    }

  

    // Getters and Setters
    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SurveyTemplates getSurveyTemplate() {
        return surveyTemplate;
    }

    public void setSurveyTemplate(SurveyTemplates surveyTemplate) {
        this.surveyTemplate = surveyTemplate;
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

    public List<ResponseAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ResponseAnswer> answers) {
        this.answers = answers;
    }
    
}