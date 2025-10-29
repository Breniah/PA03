package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryResourceStore {

    private final Map<String, ResourceDTO> byId = new HashMap<>();
    private final List<ResourceDTO> all = new ArrayList<>();

    public InMemoryResourceStore() {
        // Seed data
        List<ResourceDTO> seed = List.of(
                new ResourceDTO("1", "Math Tutoring Center", "Tutoring", "Building A, Room 101", "https://famu.edu/tutoring", List.of("math", "help", "students")),
                new ResourceDTO("2", "Writing Lab", "Lab", "Building B, Room 202", "https://famu.edu/writing", List.of("writing", "english", "composition")),
                new ResourceDTO("3", "Career Advising", "Advising", "Building C, Room 303", "https://famu.edu/career", List.of("career", "advising", "jobs")),
                new ResourceDTO("4", "Computer Lab", "Lab", "Building D, Room 404", "https://famu.edu/computerlab", List.of("computers", "printing")),
                new ResourceDTO("5", "Science Tutoring", "Tutoring", "Building E, Room 505", "https://famu.edu/sciencehelp", List.of("biology", "chemistry"))
        );

        for (ResourceDTO r : seed) {
            byId.put(r.id(), r);
            all.add(r);
        }

        System.out.println("[INFO] Seeded " + all.size() + " resources.");
    }

    public List<ResourceDTO> findAll() {
        return List.copyOf(all);
    }

    public Optional<ResourceDTO> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<ResourceDTO> findByFilters(Optional<String> category, Optional<String> q) {
        return all.stream()
                .filter(r -> category.map(c -> r.category().equalsIgnoreCase(c)).orElse(true))
                .filter(r -> q.map(query -> {
                    String lower = query.toLowerCase();
                    return r.name().toLowerCase().contains(lower) ||
                            r.tags().stream().anyMatch(tag -> tag.toLowerCase().contains(lower));
                }).orElse(true))
                .collect(Collectors.toList());
    }
}
