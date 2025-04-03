package com.example.EmployeeWellnessTracker.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EmployeeWellnessTracker.dto.SurveyResponseDto;
import com.example.EmployeeWellnessTracker.dto.SurveyResponseRequest;
import com.example.EmployeeWellnessTracker.models.Questions;
import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.example.EmployeeWellnessTracker.models.SurveyResponse;
import com.example.EmployeeWellnessTracker.models.SurveyTemplates;
import com.example.EmployeeWellnessTracker.models.User;
import com.example.EmployeeWellnessTracker.repository.QuestionRepository;
import com.example.EmployeeWellnessTracker.repository.ResponseAnswerRepository;
import com.example.EmployeeWellnessTracker.repository.SurveyResponseRepository;
import com.example.EmployeeWellnessTracker.repository.SurveyTemplateRepository;
import com.example.EmployeeWellnessTracker.repository.UserRepository;

@Service
public class SurveyResponseService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SurveyTemplateRepository surveyTemplateRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private ResponseAnswerRepository responseAnswerRepository;


@Transactional
    public String submitSurveyResponse(SurveyResponseRequest request) {
        // Validate user
        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            return "User not found.";
        }

        // Validate survey template
        Optional<SurveyTemplates> surveyTemplateOptional = surveyTemplateRepository.findById(request.getTemplateId());
        if (surveyTemplateOptional.isEmpty()) {
            return "Survey template not found.";
        }

        User user = userOptional.get();
        SurveyTemplates surveyTemplate = surveyTemplateOptional.get();

        // Create new survey response
        SurveyResponse surveyResponse = new SurveyResponse(user, surveyTemplate);
        surveyResponse.setEditableUntil(LocalDateTime.now().plusMinutes(30)); // Set edit window
        surveyResponse = surveyResponseRepository.save(surveyResponse); // Save response first

        // Save answers
        for (SurveyResponseRequest.AnswerRequest answerRequest : request.getAnswers()) {
            Optional<Questions> questionOptional = questionRepository.findById(answerRequest.getQuestionId());
            if (questionOptional.isPresent()) {
                ResponseAnswer responseAnswer = new ResponseAnswer(surveyResponse, questionOptional.get(), answerRequest.getAnswerText());
                responseAnswerRepository.save(responseAnswer);
            }
        }
        return "Survey response submitted successfully.";
    }

       // Method to delete a survey response by ID
        public void deleteSurveyResponse(Long responseId) {
        SurveyResponse surveyResponse = surveyResponseRepository.findById(responseId)
            .orElseThrow(() -> new IllegalArgumentException("Survey Response not found"));

            LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(surveyResponse.getEditableUntil())) {
        throw new IllegalStateException("Survey response can only be edited within 5 minutes of submission.");
    }
        surveyResponseRepository.delete(surveyResponse);
}


// Get Survey Response by ID
public SurveyResponse getSurveyResponseById(Long responseId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
        .orElseThrow(NoSuchElementException::new); // Built-in Java Exception

    SurveyResponse response = surveyResponseRepository.findById(responseId)
        .orElseThrow(NoSuchElementException::new);

    if (!response.getUser().equals(user)) {
        throw new AccessDeniedException("Unauthorized to access this response");
    }

    return response;
}

//Update Survey Response
@Transactional
    public SurveyResponse updateSurveyResponse(Long responseId, List<ResponseAnswer> updatedAnswers) {
        SurveyResponse surveyResponse = surveyResponseRepository.findById(responseId)
            .orElseThrow(() -> new RuntimeException("Survey Response not found"));

                //  Check if the response is still editable
    LocalDateTime currentTime = LocalDateTime.now();
    if (currentTime.isAfter(surveyResponse.getEditableUntil())) {
        throw new IllegalStateException("Survey response can only be edited within 5 minutes of submission.");
    }

        for (ResponseAnswer updatedAnswer : updatedAnswers) {
            Questions question = questionRepository.findById(updatedAnswer.getQuestion().getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + updatedAnswer.getQuestion().getQuestionId()));

            ResponseAnswer responseAnswer = responseAnswerRepository.findBySurveyResponseAndQuestion(surveyResponse, question)
                .orElse(new ResponseAnswer());

            responseAnswer.setSurveyResponse(surveyResponse);
            responseAnswer.setQuestion(question);  // ✅ Ensure question is set properly
            responseAnswer.setAnswerText(updatedAnswer.getAnswerText());

            responseAnswerRepository.save(responseAnswer);
        }
        
        return surveyResponse;
    }

    //Get Past Submission (Logged in  User)
    public List<SurveyResponseDto> getUserSubmissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<SurveyResponse> submissions = surveyResponseRepository.findByUser_UserId(user.get().getUserId());

        return submissions.stream()
                .map(SurveyResponseDto::new)
                .collect(Collectors.toList());
    }


}