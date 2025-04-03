
package com.example.EmployeeWellnessTracker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.EmployeeWellnessTracker.models.Questions;
import com.example.EmployeeWellnessTracker.models.SurveyTemplates;
import com.example.EmployeeWellnessTracker.repository.QuestionRepository;
import com.example.EmployeeWellnessTracker.repository.SurveyTemplateRepository;

@Service
public class SurveyTemplateService {

    @Autowired
    private SurveyTemplateRepository surveyTemplateRepository;


    @Autowired
    private QuestionRepository questionRepository;

    //Create Survey Template(Admin)
    public SurveyTemplates createSurveyTemplate(SurveyTemplates surveyTemplate) {
        SurveyTemplates savedTemplate = surveyTemplateRepository.save(surveyTemplate);
        if (surveyTemplate.getQuestions() != null) {
            for (Questions question : surveyTemplate.getQuestions()) {
                question.setSurveyTemplate(savedTemplate); 
                questionRepository.save(question);
            }
        }

        return savedTemplate;
    }

   //Update Survey Template(Admin)
    public SurveyTemplates updateSurveyTemplate(Long templateId, SurveyTemplates updatedTemplate) {
        SurveyTemplates existingTemplate = surveyTemplateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found with ID"));

        // ✅ Update template name and category
        existingTemplate.setTemplateName(updatedTemplate.getTemplateName());
        existingTemplate.setCategory(updatedTemplate.getCategory());

        // ✅ Updating questions (modify existing or add new)
        if (updatedTemplate.getQuestions() != null) {
            for (Questions updatedQuestion : updatedTemplate.getQuestions()) {
                if (updatedQuestion.getQuestionId() != null) {
                    // Modify existing question
                    Questions existingQuestion = questionRepository.findById(updatedQuestion.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found with ID: " + updatedQuestion.getQuestionId()));

                    existingQuestion.setQuestionText(updatedQuestion.getQuestionText());
                    existingQuestion.setOption1(updatedQuestion.getOption1());
                    existingQuestion.setOption2(updatedQuestion.getOption2());
                    existingQuestion.setOption3(updatedQuestion.getOption3());
                    existingQuestion.setOption4(updatedQuestion.getOption4());
                    questionRepository.save(existingQuestion);
                } else {
                    // Add new question
                    updatedQuestion.setSurveyTemplate(existingTemplate);
                    questionRepository.save(updatedQuestion);
                }
            }
        }

        return surveyTemplateRepository.save(existingTemplate);
    }

    //Delete Template (Admin)
    public ResponseEntity<String> deleteSurveyTemplate(Long id) {
        return surveyTemplateRepository.findById(id).map(surveyTemplate -> {
            surveyTemplateRepository.delete(surveyTemplate);
            return ResponseEntity.ok("Survey Template deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }


  //Delete Question (Admin)
    public ResponseEntity<String> deleteQuestion(Long id) {
        return questionRepository.findById(id).map(question -> {
            questionRepository.delete(question);
            return ResponseEntity.ok("Question deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    //Get All Survey(Admin/Employee)
    public List<SurveyTemplates> getAvailableSurveyTemplates() {
        return surveyTemplateRepository.findAll(); // You can modify this method as needed
    }

    // Get Survey by ID (Admin/Employee)
    public Optional<SurveyTemplates> getSurveyById(Long id) {
        return surveyTemplateRepository.findById(id);
    }


    

}




