package com.example.EmployeeWellnessTracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.EmployeeWellnessTracker.models.Questions;
import com.example.EmployeeWellnessTracker.models.ResponseAnswer;
import com.example.EmployeeWellnessTracker.models.SurveyResponse;

public interface ResponseAnswerRepository extends JpaRepository<ResponseAnswer, Long> {

     Optional<ResponseAnswer> findBySurveyResponseAndQuestion(SurveyResponse surveyResponse, Questions question);

     @Modifying
     @Query("UPDATE ResponseAnswer r SET r.question = NULL WHERE r.question.questionId = :questionId")
     void updateQuestionIdToNull(@Param("questionId") Long questionId);
}