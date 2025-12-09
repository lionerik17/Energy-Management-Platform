package com.example.backend_user.controllers;

import com.example.backend_user.dtos.UserDTO;
import com.example.backend_user.dtos.UserResponse;
import com.example.backend_user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for user management and profile access")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user profile and returns the created resource location.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "User with same credentials already exists", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserDTO dto) {
        UserResponse user = userService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.id())
                .toUri();

        return ResponseEntity.created(location).body(user);
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates user credentials by user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer id,
            @Valid @RequestBody UserDTO dto) {
        UserResponse user= userService.update(id, dto);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Update an existing user's profile",
            description = "Updates user age by user ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body
    ) {
        Integer age = (Integer) body.get("age");
        return ResponseEntity.ok(userService.updateAge(id, age));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user account by ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all clients",
            description = "Retrieves all users that have the CLIENT role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllClients() {
        return ResponseEntity.ok(userService.findAllClients());
    }

    @Operation(
            summary = "Get all admins",
            description = "Retrieves all users that have the ADMIN role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
            }
    )
    @GetMapping("/admins")
    public ResponseEntity<List<UserResponse>> getAllAdmins() {
        return ResponseEntity.ok(userService.findAllAdmins());
    }

    @Operation(
            summary = "Get a specific user by their ID",
            description = "Retrieves user with specified ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Retrieved user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
            summary = "Get details of the authenticated user",
            description = "Returns the currently authenticated user's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Missing X-User-Id header", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @GetMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(
            @Parameter(description = "User ID extracted from JWT",
                    example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
