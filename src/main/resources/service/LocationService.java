package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.exception.ConflictException;
import edu.famu.cop3060.resources.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LocationService {

    private final Map<Long, LocationDTO> locations = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);
    private final ResourceService resourceService;

    public LocationService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public LocationDTO create(LocationDTO dto) {
        Long id = idCounter.incrementAndGet();
        dto.setId(id);
        locations.put(id, dto);
        return dto;
    }

    public List<LocationDTO> getAll() {
        return new ArrayList<>(locations.values());
    }

    public LocationDTO getById(Long id) {
        LocationDTO loc = locations.get(id);
        if (loc == null) throw new NotFoundException("Location " + id + " not found");
        return loc;
    }

    public LocationDTO update(Long id, LocationDTO dto) {
        LocationDTO existing = getById(id);
        existing.setName(dto.getName());
        return existing;
    }

    public void delete(Long id) {
        long usageCount = resourceService.getAll().stream()
                .filter(r -> r.location().equalsIgnoreCase(String.valueOf(id)))
                .count();
        if (usageCount > 0)
            throw new ConflictException("Location " + id + " is used by " + usageCount + " resources");

        if (locations.remove(id) == null)
            throw new NotFoundException("Location " + id + " not found");
    }
}
