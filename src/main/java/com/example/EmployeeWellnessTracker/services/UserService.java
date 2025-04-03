package com.example.EmployeeWellnessTracker.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.EmployeeWellnessTracker.models.User;
import com.example.EmployeeWellnessTracker.repository.UserRepository;
import com.example.EmployeeWellnessTracker.utils.JwtUtils;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // Create a new user (Registration) (Public)
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       
        user.setRole("EMPLOYEE");
        return userRepository.save(user);
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

     //Login (Public)
    public String authenticate(User user) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        User existingUser = userRepository.findByEmail(user.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (!existingUser.isEnabled()) { // ✅ Automatically checks isActive
        throw new RuntimeException("Account is deactivated. Please contact support.");
    }
        return jwtUtil.generateToken(userDetails);
    }

    //Get current userdetails (User)
    public User getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(email);
        return currentUser.orElse(null);
    }

    // Update user details(User)
    public User updateUser(User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user = existingUser.get();
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(userDetails.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
        } return userRepository.save(user);
    } 
    // Deactivate account (User)
    public String deactivateLoggedInEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> currentUser = userRepository.findByEmail(email);
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = currentUser.get();

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is already deactivated");
        }

        user.setActive(false);
        userRepository.save(user);

        SecurityContextHolder.clearContext(); // ✅ Clear security context (logout effect)

        return "Employee account deactivated successfully.";
    }

    
//Get ALL Users (Admin) 
    public List<User> getAll() {
        return userRepository.findAll();
    }

 // Delete User by ID (Admin)   
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }


//Update user role(Admin)
    public String updateUserRole(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!"EMPLOYEE".equals(user.getRole())) {
                return "Only EMPLOYEES can be promoted to ADMIN.";
            }

            user.setRole("ADMIN");
            userRepository.save(user);
            return "User role updated to ADMIN successfully.";
        } else {
            return "User not found.";
        }
    }


   //Add new user(Admin)
    public String addUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        //  Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //  Save user in database
        userRepository.save(user);
        return "Employee added successfully.";
    }


     // Get user by email(User)
     public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

    