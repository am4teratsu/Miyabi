package com.miyabi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.miyabi.models.User;
import com.miyabi.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
    
    
    // Agregado x Fabricio
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        User user = userService.findById(id);
        if (user != null) {
            user.setNames(userDetails.getNames());
            user.setSurnames(userDetails.getSurnames());
            user.setEmail(userDetails.getEmail());
            user.setState(userDetails.getState());
            user.setRol(userDetails.getRol());
            
            // Si el password sta vacio 
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }
            
            return ResponseEntity.ok(userService.save(user));
        }
        return ResponseEntity.notFound().build();
    }
    
    // Agregado x Fabricio
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        // Borrar físicamente o solo cambiar el state a 0
        userService.deleteById(id); 
    }
    
    
}