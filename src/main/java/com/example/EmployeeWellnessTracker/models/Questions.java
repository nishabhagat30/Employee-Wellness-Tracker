package com.example.EmployeeWellnessTracker.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "questions")
public class Questions{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    private String option1;

    @Column(nullable = false)
    private String option2;

    @Column(nullable = false)
    private String option3;

    @Column(nullable = false)
    private String option4;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    @JsonBackReference // Prevents infinite loop by stopping serialization here
    private SurveyTemplates surveyTemplate;

    

    public Questions() {
    }

    public Questions(String questionText, String option1, String option2, String option3,
            String option4, SurveyTemplates surveyTemplate) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.surveyTemplate = surveyTemplate;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public SurveyTemplates getSurveyTemplate() {
        return surveyTemplate;
    }

    public void setSurveyTemplate(SurveyTemplates surveyTemplate) {
        this.surveyTemplate = surveyTemplate;
    }
}