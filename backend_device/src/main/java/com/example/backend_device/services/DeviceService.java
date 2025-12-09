package com.example.backend_device.services;

import com.example.backend_device.dtos.DeviceDTO;
import com.example.backend_device.dtos.DeviceResponse;
import com.example.backend_device.entities.Device;
import com.example.backend_device.repositories.DeviceRepository;
import com.example.backend_device.repositories.DevicesUsersRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final DevicesUsersRepository devicesUsersRepository;
    private final SyncPublisher syncPublisher;

    @Transactional
    public DeviceResponse create(DeviceDTO dto) {
        Device device = Device.builder()
                .serialNumber(dto.serialNumber())
                .name(dto.name())
                .maxConsumptionValue(dto.maxConsumptionValue())
                .build();

        deviceRepository.save(device);
        syncPublisher.deviceCreated(device);

        return new DeviceResponse(device.getId(), device.getSerialNumber(), device.getName(), device.getMaxConsumptionValue());
    }

    @Transactional
    public DeviceResponse update(Integer id, DeviceDTO dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        device.setSerialNumber(dto.serialNumber());
        device.setName(dto.name());
        device.setMaxConsumptionValue(dto.maxConsumptionValue());

        deviceRepository.save(device);
        syncPublisher.deviceUpdated(device);

        return new DeviceResponse(device.getId(), device.getSerialNumber(), device.getName(), device.getMaxConsumptionValue());
    }

    @Transactional
    public void delete(Integer id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        syncPublisher.deviceDeleted(device);
        deviceRepository.delete(device);
        devicesUsersRepository.deleteByIdDevice(device.getId());
    }

    public List<DeviceResponse> findAll() {
        return deviceRepository.findAll().stream()
                .map(device -> new DeviceResponse(
                        device.getId(),
                        device.getSerialNumber(),
                        device.getName(),
                        device.getMaxConsumptionValue()
                ))
                .toList();
    }

    public List<Integer> findAllIds() {
        return deviceRepository.findAllIds();
    }
}
