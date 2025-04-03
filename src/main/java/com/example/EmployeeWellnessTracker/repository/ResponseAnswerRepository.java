package com.example.EmployeeWellnessTracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EmployeeWellnessTracker.models.Questions;
import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.example.EmployeeWellnessTracker.models.SurveyResponse;

public interface ResponseAnswerRepository extends JpaRepository<ResponseAnswer, Long> {


     Optional<ResponseAnswer> findBySurveyResponseAndQuestion(SurveyResponse surveyResponse, Questions question);
}