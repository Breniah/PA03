package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.*;
import edu.famu.cop3060.resources.exception.InvalidReferenceException;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResourcesService {

    private final InMemoryResourceStore store;
    private final LocationService locationService;
    private final CategoryService categoryService;

    public ResourcesService(InMemoryResourceStore store,
                            LocationService locationService,
                            CategoryService categoryService) {
        this.store = store;
        this.locationService = locationService;
        this.categoryService = categoryService;
    }

    public List<ResourceDTO> getResources(Optional<String> category, Optional<String> q) {
        return store.findByFilters(category, q);
    }

    public Optional<ResourceDTO> getResourceById(String id) {
        return store.findById(id);
    }

    public ResourceDTO create(ResourceDTO dto) {
        // Validate references
        locationService.getById(dto.getLocationId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid locationId: " + dto.getLocationId()));
        categoryService.getById(dto.getCategoryId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid categoryId: " + dto.getCategoryId()));

        ResourceDTO created = store.create(dto);
        locationService.incrementUsage(dto.getLocationId());
        categoryService.incrementUsage(dto.getCategoryId());
        return expand(created);
    }

    public ResourceDTO update(String id, ResourceDTO dto) {
        store.findById(id).orElseThrow(() -> new NotFoundException("Resource not found: " + id));
        locationService.getById(dto.getLocationId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid locationId: " + dto.getLocationId()));
        categoryService.getById(dto.getCategoryId())
                .orElseThrow(() -> new InvalidReferenceException("Invalid categoryId: " + dto.getCategoryId()));
        store.update(id, dto);
        return expand(dto);
    }

    public void delete(String id) {
        ResourceDTO existing = store.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found: " + id));
        store.delete(id);
        locationService.decrementUsage(existing.getLocationId());
        categoryService.decrementUsage(existing.getCategoryId());
    }

    private ResourceDTO expand(ResourceDTO dto) {
        LocationDTO loc = locationService.getById(dto.getLocationId()).orElse(null);
        CategoryDTO cat = categoryService.getById(dto.getCategoryId()).orElse(null);
        // For debugging/logging you can attach nested expansions in a wrapper
        return dto;
    }
}

