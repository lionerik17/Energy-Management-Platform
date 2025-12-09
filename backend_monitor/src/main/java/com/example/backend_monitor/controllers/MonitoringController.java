package com.example.backend_monitor.controllers;

import com.example.backend_monitor.dtos.HourlyConsumptionResponse;
import com.example.backend_monitor.services.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring", description = "Endpoints for energy consumption monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;

    @Operation(
            summary = "Get complete consumption history for a device",
            description = "Returns all hourly consumption entries recorded for the specified device.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "History retrieved successfully",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = HourlyConsumptionResponse.class)
                            ))),
                    @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
            }
    )
    @GetMapping("/{deviceId}")
    public List<HourlyConsumptionResponse> getDeviceHistory(
            @Parameter(description = "The ID of the device", example = "3")
            @PathVariable Integer deviceId
    ) {
        return monitoringService.getDeviceConsumption(deviceId);
    }

    @Operation(
            summary = "Get consumption history for all devices",
            description = "Returns complete hourly consumption history for every registered device.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All histories retrieved successfully",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = HourlyConsumptionResponse.class)
                            )))
            }
    )
    @GetMapping
    public List<HourlyConsumptionResponse> getAllDevicesHistory() {
        return monitoringService.getAllDevicesConsumption();
    }

    @Operation(
            summary = "Get daily consumption for a device",
            description = "Returns hourly consumption for a device for the specified day, starting from 00:00 to 23:59.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Daily history retrieved successfully",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = HourlyConsumptionResponse.class)
                            ))),
                    @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
            }
    )
    @GetMapping("/day/{deviceId}")
    public List<HourlyConsumptionResponse> getDeviceConsumptionForDay(
            @Parameter(description = "The ID of the device", example = "3")
            @PathVariable Integer deviceId,

            @Parameter(description = "Date in ISO format (YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return monitoringService.getConsumptionForDay(deviceId, date);
    }
}
