
package com.example.EmployeeWellnessTracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.EmployeeWellnessTracker.models.SurveyTemplates;

@Repository
public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplates, Long> {
}
