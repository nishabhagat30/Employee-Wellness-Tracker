package com.example.EmployeeWellnessTracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.EmployeeWellnessTracker.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query to find a user by email
    Optional<User> findByEmail(String email);

    
    @Transactional
    @Modifying
    void deleteByEmail(String email);
   
    // ✅ Check if email already exists
    boolean existsByEmail(String email);
   

}
