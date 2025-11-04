package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import edu.famu.cop3060.resources.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CategoryService {

    private final Map<Long, CategoryDTO> store = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    // ResourceService will call this to check if a Category is in use
    private final Map<Long, Long> usageCount = new HashMap<>();

    public CategoryDTO create(CategoryDTO dto) {
        Long id = idGen.incrementAndGet();
        dto.setId(id);
        store.put(id, dto);
        return dto;
    }

    public List<CategoryDTO> getAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<CategoryDTO> getById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        if (!store.containsKey(id)) throw new NoSuchElementException("Category not found");
        dto.setId(id);
        store.put(id, dto);
        return dto;
    }

    public void delete(Long id) {
        if (usageCount.getOrDefault(id, 0L) > 0)
            throw new ConflictException("Category " + id + " is in use by " + usageCount.get(id) + " resources");
        store.remove(id);
    }

    // Helper for ResourceService to track usage
    public void incrementUsage(Long categoryId) {
        usageCount.merge(categoryId, 1L, Long::sum);
    }

    public void decrementUsage(Long categoryId) {
        usageCount.computeIfPresent(categoryId, (k, v) -> v > 1 ? v - 1 : null);
    }
}
