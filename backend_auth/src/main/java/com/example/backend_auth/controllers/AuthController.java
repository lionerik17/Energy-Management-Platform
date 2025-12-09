package com.example.backend_auth.controllers;

import com.example.backend_auth.dtos.*;
import com.example.backend_auth.services.AuthService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Endpoints for registration, login, and token verification"
)
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new account and returns an authentication response containing a JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully registered",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDTO req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(
            summary = "Update user authentication data",
            description = "Updates a user's authentication details such as password or role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<CredentialsResponse> update(
            @Parameter(description = "ID of the user to update", example = "1") @PathVariable Integer id,
            @Valid @RequestBody AuthDTO dto) {
        CredentialsResponse cred = authService.update(id, dto);
        return ResponseEntity.ok(cred);
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user account by username.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of user to delete", example = "1")
            @PathVariable Integer id) {
        authService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user with username and password, returning a JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @Operation(
            summary = "Verify token validity",
            description = "Checks if the provided JWT token is valid.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token is valid"),
                    @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content)
            }
    )
    @GetMapping("/verify")
    public ResponseEntity<Void> verify(
            @Parameter(description = "Bearer JWT token")
            @RequestHeader(value = "Authorization", required = false) String header) {
        Claims claims = authService.verifyAndExtract(header);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer userId = claims.get("id", Integer.class);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        return ResponseEntity.ok()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User", username)
                .header("X-Role", role)
                .build();
    }

    @Operation(
            summary = "Authorize user by role",
            description = "Verifies if a user (based on JWT token) has the required role for access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User has the required role"),
                    @ApiResponse(responseCode = "403", description = "User does not have the required role", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid or missing token", content = @Content)
            }
    )
    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize(
            @Parameter(description = "Bearer JWT token")
            @RequestHeader(value = "Authorization", required = false) String header,
            @Parameter(description = "Required role for authorization", example = "ADMIN")
            @RequestParam String role
    ) {
        return authService.authorizeRequest(header, role)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
