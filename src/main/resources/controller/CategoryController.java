package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import edu.famu.cop3060.resources.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.Comparator;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/categories/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort
    ) {
        List<CategoryDTO> all = service.getAll();
        List<CategoryDTO> sorted = sortCategories(all, sort);
        List<CategoryDTO> paged = paginate(sorted, page, size);

        Map<String, Object> envelope = Map.of(
                "content", paged,
                "page", page,
                "size", size,
                "totalElements", all.size(),
                "totalPages", (int) Math.ceil((double) all.size() / size)
        );

        return ResponseEntity.ok(envelope);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id,
                                              @Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private List<CategoryDTO> sortCategories(List<CategoryDTO> list, String sort) {
        Comparator<CategoryDTO> comp = Comparator.comparing(CategoryDTO::getName, Comparator.nullsLast(String::compareToIgnoreCase));
        if (sort.startsWith("-")) comp = comp.reversed();
        return list.stream().sorted(comp).toList();
    }

    private List<CategoryDTO> paginate(List<CategoryDTO> list, int page, int size) {
        int from = Math.min(page * size, list.size());
        int to = Math.min(from + size, list.size());
        return list.subList(from, to);
    }
}
