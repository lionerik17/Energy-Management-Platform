package com.example.backend_device.controllers;

import com.example.backend_device.dtos.DeviceResponse;
import com.example.backend_device.dtos.DevicesUsersResponse;
import com.example.backend_device.services.DevicesUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices-users")
@RequiredArgsConstructor
@Tag(
        name = "Devices Users",
        description = "Endpoints for managing the association between users and devices"
)
public class DevicesUsersController {

    private final DevicesUsersService devicesUsersService;

    @Operation(
            summary = "Assign a device to a user",
            description = "Links a device to a user by their IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device successfully assigned"),
                    @ApiResponse(responseCode = "404", description = "User or device not found", content = @Content)
            }
    )
    @PostMapping("/assign/{userId}/{deviceId}")
    public ResponseEntity<String> assign(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer userId,
            @Parameter(description = "Device ID", example = "1") @PathVariable Integer deviceId) {
        devicesUsersService.assign(userId, deviceId);
        return ResponseEntity.ok("Device assigned");
    }

    @Operation(
            summary = "Unassign a specific device from a user",
            description = "Removes the association between a given device and user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device unassigned successfully"),
                    @ApiResponse(responseCode = "404", description = "Association not found", content = @Content)
            }
    )
    @DeleteMapping("/unassign/{userId}/{deviceId}")
    public ResponseEntity<String> unassign(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer userId,
            @Parameter(description = "Device ID", example = "1") @PathVariable Integer deviceId) {
        devicesUsersService.unassign(userId, deviceId);
        return ResponseEntity.ok("Device unassigned for user " + userId);
    }

    @Operation(
            summary = "Unassign all devices from a user",
            description = "Removes all device associations for the specified user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All devices unassigned successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @DeleteMapping("/unassign-all/{userId}")
    public ResponseEntity<String> unassignAll(
            @Parameter(description = "User ID", example = "1") @PathVariable Integer userId) {
        devicesUsersService.unassignAll(userId);
        return ResponseEntity.ok("Devices unassigned for user" + userId);
    }

    @Operation(
            summary = "Get all devices assigned to the current user",
            description = "Retrieves all devices linked to the authenticated user, based on their JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Devices retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping("/details")
    public ResponseEntity<List<DeviceResponse>> getUserDevices(
            @Parameter(description = "User ID", example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<DeviceResponse> devices = devicesUsersService.getUserDevices(userId);
            return ResponseEntity.ok(devices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Get all users and their assigned devices",
            description = """
                    Returns a list where each entry represents a user and the devices assigned to that user.
                    Uses event-driven projections to include username and device details.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of user–device associations",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DevicesUsersResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – missing or invalid token"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – admin access required"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<DevicesUsersResponse>> getAllAssignments() {
        return ResponseEntity.ok(devicesUsersService.getAllUsersWithDevices());
    }
}
