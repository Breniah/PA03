package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.service.ResourcesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.Comparator;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {

    private static final Logger logger = LoggerFactory.getLogger(ResourcesController.class);
    private final ResourcesService service;

    public ResourcesController(ResourcesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllResources(
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort
    ) {
        logger.info("GET /api/resources - filters: category={}, q={}, page={}, size={}, sort={}",
                category.orElse("none"), q.orElse("none"), page, size, sort);

        List<ResourceDTO> filtered = service.getResources(category, q);
        List<ResourceDTO> sorted = sortResources(filtered, sort);
        List<ResourceDTO> paged = paginate(sorted, page, size);

        Map<String, Object> envelope = Map.of(
                "content", paged,
                "page", page,
                "size", size,
                "totalElements", filtered.size(),
                "totalPages", (int) Math.ceil((double) filtered.size() / size)
        );

        return ResponseEntity.ok(envelope);
    }

    @GetMapping("/{id}")
    public ResourceDTO getResourceById(@PathVariable String id) {
        logger.info("GET /api/resources/{}", id);
        return service.getResourceById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found: " + id));
    }

    @PostMapping
    public ResponseEntity<ResourceDTO> create(@Valid @RequestBody ResourceDTO resourceDTO) {
        logger.info("POST /api/resources - creating resource: {}", resourceDTO);
        ResourceDTO created = service.create(resourceDTO);
        return ResponseEntity.created(URI.create("/api/resources/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(@PathVariable String id,
                                              @Valid @RequestBody ResourceDTO resourceDTO) {
        logger.info("PUT /api/resources/{} - updating resource: {}", id, resourceDTO);
        ResourceDTO updated = service.update(id, resourceDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        logger.info("DELETE /api/resources/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Helper functions for sort/paging
    private List<ResourceDTO> sortResources(List<ResourceDTO> list, String sort) {
        Comparator<ResourceDTO> comparator = Comparator.comparing(ResourceDTO::name, Comparator.nullsLast(String::compareToIgnoreCase));
        if (sort.startsWith("-")) comparator = comparator.reversed();
        return list.stream().sorted(comparator).toList();
    }

    private List<ResourceDTO> paginate(List<ResourceDTO> list, int page, int size) {
        int from = Math.min(page * size, list.size());
        int to = Math.min(from + size, list.size());
        return list.subList(from, to);
    }
}


