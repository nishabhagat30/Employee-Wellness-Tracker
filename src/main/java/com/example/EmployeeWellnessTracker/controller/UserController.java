package com.example.EmployeeWellnessTracker.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.models.User;
import com.example.EmployeeWellnessTracker.services.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

   // Update user details
        @PutMapping
        public ResponseEntity<?> updateUser(@RequestBody User userDetails) {
            User updatedUser = userService.updateUser(userDetails);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body("User not found or unauthorized to update.");
            }
           
        }


   // Deactiavte
    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivateLoggedInEmployee() {
        String message = userService.deactivateLoggedInEmployee();
        return ResponseEntity.ok(message);
    }


    //Get Current User Profile
     @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        User user = userService.getCurrentUserProfile();
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    
   // Get user by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
}
