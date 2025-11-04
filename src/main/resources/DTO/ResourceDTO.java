package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResourceDTO {

    private String id;

    @NotBlank(message = "Resource name is required")
    private String name;

    @NotNull(message = "locationId is required")
    private Long locationId;

    @NotNull(message = "categoryId is required")
    private Long categoryId;

    private String url;

    public ResourceDTO() {}

    public ResourceDTO(String id, String name, Long locationId, Long categoryId, String url) {
        this.id = id;
        this.name = name;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.url = url;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}


