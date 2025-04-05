
package com.example.EmployeeWellnessTracker.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EmployeeWellnessTracker.models.Questions;
import com.example.EmployeeWellnessTracker.models.SurveyTemplates;
import com.example.EmployeeWellnessTracker.repository.QuestionRepository;
import com.example.EmployeeWellnessTracker.repository.SurveyTemplateRepository;
import com.example.EmployeeWellnessTracker.repository.ResponseAnswerRepository;

@Service
public class SurveyTemplateService {

    @Autowired
    private SurveyTemplateRepository surveyTemplateRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseAnswerRepository responseAnswerRepository;

    // Create Survey Template(Admin)
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

    @Transactional
    public String updateSurveyTemplate(Long templateId, String newName, String newCategory, List<Questions> updatedQuestions) {
        Optional<SurveyTemplates> templateOptional = surveyTemplateRepository.findById(templateId);
    
        if (templateOptional.isEmpty()) {
            return "Survey template not found.";
        }
    
        SurveyTemplates template = templateOptional.get();
    
        if (newName != null && !newName.isEmpty()) {
            template.setTemplateName(newName);
        }
        if (newCategory != null && !newCategory.isEmpty()) {
            template.setCategory(newCategory);
        }
    
        List<Questions> existingQuestions = template.getQuestions();
        List<Questions> newQuestions = new ArrayList<>();
    
        for (Questions existingQ : new ArrayList<>(existingQuestions)) { 
            boolean isStillPresent = updatedQuestions.stream()
                .anyMatch(newQ -> newQ.getQuestionId() != null && newQ.getQuestionId().equals(existingQ.getQuestionId()));
    
            if (!isStillPresent) {
                responseAnswerRepository.updateQuestionIdToNull(existingQ.getQuestionId()); 
                questionRepository.deleteById(existingQ.getQuestionId()); 
                existingQuestions.remove(existingQ);
            }
        }

        for (Questions updatedQ : updatedQuestions) {
            if (updatedQ.getQuestionId() == null) {
                updatedQ.setSurveyTemplate(template);
                newQuestions.add(updatedQ);
                continue;
            }
    
            Optional<Questions> match = existingQuestions.stream()
                .filter(q -> q.getQuestionId() != null && q.getQuestionId().equals(updatedQ.getQuestionId()))
                .findFirst();
    
            if (match.isPresent()) {
                match.get().setQuestionText(updatedQ.getQuestionText());
                match.get().setOption1(updatedQ.getOption1());
                match.get().setOption2(updatedQ.getOption2());
                match.get().setOption3(updatedQ.getOption3());
                match.get().setOption4(updatedQ.getOption4());
            }
        }
    
        newQuestions = questionRepository.saveAll(newQuestions);
        existingQuestions.addAll(newQuestions);
        template.setQuestions(existingQuestions);
        surveyTemplateRepository.save(template);
    
        return "Survey template updated successfully.";
    }

    // Delete Template (Admin)
    public ResponseEntity<String> deleteSurveyTemplate(Long id) {
        return surveyTemplateRepository.findById(id).map(surveyTemplate -> {
            surveyTemplateRepository.delete(surveyTemplate);
            return ResponseEntity.ok("Survey Template deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete Question (Admin)
    public ResponseEntity<String> deleteQuestion(Long id) {
        return questionRepository.findById(id).map(question -> {
            questionRepository.delete(question);
            return ResponseEntity.ok("Question deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Get All Survey(Admin/Employee)
    public List<SurveyTemplates> getAvailableSurveyTemplates() {
        return surveyTemplateRepository.findAll(); // You can modify this method as needed
    }

    // Get Survey by ID (Admin/Employee)
    public Optional<SurveyTemplates> getSurveyById(Long id) {
        return surveyTemplateRepository.findById(id);
    }

}
