package com.example.backend_device.controllers;

import com.example.backend_device.dtos.DeviceDTO;
import com.example.backend_device.dtos.DeviceResponse;
import com.example.backend_device.services.DeviceService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
@Tag(name = "Devices", description = "Endpoints for managing devices")
public class DeviceController {
    private final DeviceService deviceService;

    @Operation(
            summary = "Create a new device",
            description = "Registers a new device in the system and returns the created resource location",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Device created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Device with same serial already exists", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceDTO dto) {
        DeviceResponse device = deviceService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(device.id())
                .toUri();

        return ResponseEntity.created(location).body(device);
    }

    @Operation(
            summary = "Update an existing device",
            description = "Updates the details of an existing device by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Device updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> update(
            @Parameter(description = "Device ID", example = "1") @PathVariable Integer id,
            @Valid @RequestBody DeviceDTO dto) {
        DeviceResponse device = deviceService.update(id, dto);
        return ResponseEntity.ok(device);
    }

    @Operation(
            summary = "Delete a device",
            description = "Deletes a device by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Device ID", example = "1") @PathVariable Integer id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all devices",
            description = "Retrieves a list of all registered devices",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of devices",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @Operation(
            summary = "Get all devices id's",
            description = "Retrieves a list of id's of all registered devices",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of devices",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceResponse.class)))
            }
    )
    @GetMapping("/ids")
    public ResponseEntity<List<Integer>> getDeviceIds() {
        return ResponseEntity.ok(deviceService.findAllIds());
    }
}
