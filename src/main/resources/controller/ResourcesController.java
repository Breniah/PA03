package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.service.ResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {

    private static final Logger logger = LoggerFactory.getLogger(ResourcesController.class);
    private final ResourcesService service;

    public ResourcesController(ResourcesService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResourceDTO> getAllResources(@RequestParam Optional<String> category,
                                             @RequestParam Optional<String> q) {
        logger.info("GET /api/resources - filters: category={}, q={}", category.orElse("none"), q.orElse("none"));
        return service.getResources(category, q);
    }

    @GetMapping("/{id}")
    public ResourceDTO getResourceById(@PathVariable String id) {
        logger.info("GET /api/resources/{}", id);
        return service.getResourceById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @ResponseStatus(code = org.springframework.http.HttpStatus.NOT_FOUND)
    private static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String id) {
            super("Resource not found: " + id);
        }
    }
}
