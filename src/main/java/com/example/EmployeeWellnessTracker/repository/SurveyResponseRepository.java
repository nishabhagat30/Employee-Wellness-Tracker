package com.example.EmployeeWellnessTracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.EmployeeWellnessTracker.models.SurveyResponse;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {


// Custom query to fetch submissions by user ID
List<SurveyResponse> findByUser_UserId(Long userId);

List<SurveyResponse> findBySubmittedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);


}
