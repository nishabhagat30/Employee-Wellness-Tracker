package com.example.EmployeeWellnessTracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.models.User;
import com.example.EmployeeWellnessTracker.services.UserService;



@RestController
@RequestMapping("/public")
public class PublicController {
    
@Autowired
private UserService userService;


private static final Logger log = LoggerFactory.getLogger(PublicController.class);


  // Create a new user (Registration)
  @PostMapping("/register")
  public ResponseEntity<?> createUser(@RequestBody User user) {

    String allowedDomain = "@example.com"; // ✅ Replace with your company's domain

    if (!user.getEmail().endsWith(allowedDomain)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Registration restricted to company email addresses");
    }

     if (userService.userExists(user.getEmail())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }
      User savedUser = userService.createUser(user);
      return ResponseEntity.ok(savedUser);
  }

//Login
@PostMapping("/login")
public ResponseEntity<String> login(@RequestBody User user) {
    try {
        String jwt = userService.authenticate(user);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    } catch (Exception e) {
        log.error("Exception occurred while authenticating user", e);
        return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
    }
}

   
}

