package com.example.EmployeeWellnessTracker.services;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.example.EmployeeWellnessTracker.models.SurveyResponse;
import com.example.EmployeeWellnessTracker.repository.SurveyResponseRepository;

@Service
public class SurveyReportService {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;
    
//Generate wellness Report (User)
    public Map<String, Map<String, Map<String, Integer>>> getWellnessReport(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<SurveyResponse> responses = surveyResponseRepository.findBySubmittedAtBetween(startDateTime, endDateTime);
  
        Map<String, Map<String, Map<String, Integer>>> report = new LinkedHashMap<>();
    
        for (SurveyResponse response : responses) {
            for (ResponseAnswer ra : response.getAnswers()) {
            if (ra.getQuestion() == null || 
            ra.getQuestion().getSurveyTemplate() == null || 
            ra.getAnswerText() == null) {
            continue;
        }
                String category = ra.getQuestion().getSurveyTemplate().getCategory();
                String question = ra.getQuestion().getQuestionText();
                String answer = ra.getAnswerText();
    
                report.computeIfAbsent(category, k -> new LinkedHashMap<>())
                      .computeIfAbsent(question, k -> new LinkedHashMap<>())
                      .merge(answer, 1, Integer::sum);
            }
        }
    
        return report;
    }
}

   