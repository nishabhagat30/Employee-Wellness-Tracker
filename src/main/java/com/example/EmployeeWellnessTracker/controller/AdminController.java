package com.example.EmployeeWellnessTracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EmployeeWellnessTracker.models.User;
import com.example.EmployeeWellnessTracker.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

@Autowired
 private UserService userService;

 // Get All users 
 @GetMapping("/all-users")
 public ResponseEntity<List<User>> getAllUsers() {
     List<User> all = userService.getAll();
     if (all == null || all.isEmpty()) {
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }
     return new ResponseEntity<>(all, HttpStatus.OK);
 }
 
 // Delete User By id
   @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    // Update User Role
    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long userId) {
        String result = userService.updateUserRole(userId);

        if ("User not found.".equals(result)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else if ("Only EMPLOYEES can be promoted to ADMIN.".equals(result)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }

    //Add New User
     @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        try {
            String message = userService.addUser(user);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
