package com.example.backend_device.dtos;

import java.util.List;

public record DevicesUsersResponse(
        UserDTO user,
        List<DeviceResponse> devices
) {}

