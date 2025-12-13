package com.example.backend_device.services;

import com.example.backend_device.dtos.DeviceResponse;
import com.example.backend_device.dtos.DevicesUsersResponse;
import com.example.backend_device.dtos.UserDTO;
import com.example.backend_device.entities.Device;
import com.example.backend_device.entities.DevicesUsers;
import com.example.backend_device.entities.UserProjection;
import com.example.backend_device.repositories.DeviceRepository;
import com.example.backend_device.repositories.DevicesUsersRepository;
import com.example.backend_device.repositories.UserProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevicesUsersService {
    private final DeviceRepository deviceRepository;
    private final UserProjectionRepository userProjectionRepository;
    private final DevicesUsersRepository devicesUsersRepo;
    private final SyncPublisher publisher;

    @Transactional
    public void assign(Integer userId, Integer deviceId) {
        DevicesUsers du = DevicesUsers.builder()
                .idUser(userId)
                .idDevice(deviceId)
                .build();

        devicesUsersRepo.save(du);
        publisher.deviceAttached(du);
    }

    @Transactional
    public void unassign(Integer userId, Integer deviceId) {
        devicesUsersRepo.deleteByIdUserAndIdDevice(userId, deviceId);
        publisher.deviceUnattached(userId, deviceId);
    }

    @Transactional
    public void unassignAll(Integer userId) {
        devicesUsersRepo.deleteByIdUser(userId);
        publisher.deviceUnattachedAll(userId);
    }

    public List<DeviceResponse> getUserDevices(Integer userId) {
        try {
            List<Device> devices = devicesUsersRepo.getDevicesForUser(userId);

            return devices.stream()
                    .map(d -> new DeviceResponse(
                            d.getId(),
                            d.getSerialNumber(),
                            d.getName(),
                            d.getMaxConsumptionValue()
                    ))
                    .toList();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    public List<DevicesUsersResponse> getAllUsersWithDevices() {
        List<DevicesUsers> links = devicesUsersRepo.findAll();

        Map<Integer, List<DevicesUsers>> grouped =
                links.stream().collect(Collectors.groupingBy(DevicesUsers::getIdUser));

        List<DevicesUsersResponse> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            Integer userId = entry.getKey();

            String username = userProjectionRepository.findById(userId)
                    .map(UserProjection::getUsername)
                    .orElse("Unknown");

            UserDTO user = new UserDTO(userId, username);

            List<DeviceResponse> devices = entry.getValue().stream()
                    .map(link -> deviceRepository.findById(link.getIdDevice()).orElse(null))
                    .filter(Objects::nonNull)
                    .map(d -> new DeviceResponse(
                            d.getId(),
                            d.getSerialNumber(),
                            d.getName(),
                            d.getMaxConsumptionValue()
                    ))
                    .toList();

            result.add(new DevicesUsersResponse(user, devices));
        }

        return result;
    }

}
