package com.example.EmployeeWellnessTracker.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "survey_templates")
public class SurveyTemplates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String templateName;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "surveyTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Questions> questions; 

    public SurveyTemplates() {
        this.createdTime = LocalDateTime.now();
    }

    public SurveyTemplates(String category, String templateName) {
        this.category = category;
        this.templateName = templateName;
        this.createdTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public List<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        this.questions = questions;
        if (questions != null) {
            for (Questions question : questions) {
                question.setSurveyTemplate(this); 
            }
        }
    }
}