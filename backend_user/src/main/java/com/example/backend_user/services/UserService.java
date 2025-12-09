package com.example.backend_user.services;

import com.example.backend_user.dtos.UserDTO;
import com.example.backend_user.dtos.UserResponse;
import com.example.backend_user.entities.User;
import com.example.backend_user.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new ResourceNotFoundException("Username already exists");
        }

        User user = User.builder()
                .id(dto.id())
                .username(dto.username())
                .role(dto.role())
                .age(dto.age())
                .build();

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getAge());
    }

    @Transactional
    public UserResponse update(Integer id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByUsername(dto.username()) && !user.getUsername().equals(dto.username())) {
            throw new ResourceNotFoundException("Username already exists");
        }

        user.setUsername(dto.username());
        user.setRole(dto.role());
        user.setAge(dto.age());

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getAge());
    }

    @Transactional
    public UserResponse updateAge(Integer id, Integer age) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        u.setAge(age);
        userRepository.save(u);

        return new UserResponse(u.getId(), u.getUsername(), u.getRole(), u.getAge());
    }

    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(user);
    }

    public List<UserResponse> findAllClients() {
        return userRepository.findAllClients().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getAge()))
                .toList();
    }

    public List<UserResponse> findAllAdmins() {
        return userRepository.findAllAdmins().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getAge()))
                .toList();
    }

    public UserResponse getUserById(Integer id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getAge());
    }
}
