
package com.example.EmployeeWellnessTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EmployeeWellnessTracker.models.Questions;

public interface QuestionRepository extends JpaRepository<Questions, Long> {


}
