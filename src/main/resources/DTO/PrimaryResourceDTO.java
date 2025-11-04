package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResourceDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long locationId;

    @NotNull
    private Long categoryId;
}
