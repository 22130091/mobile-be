package com.client.mobile.module_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {
    private Integer id;

    private Integer categoryId;
    private String categoryName;

    @NotBlank(message = "Dish name is required")
    @Size(max = 100, message = "Dish name must be less than 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String imageUrl;
    private Boolean isVegetarian = false;
    private Boolean isVegan = false;
    private Boolean isSpicy = false;
    private Integer preparationTime;
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<AllergenDTO> allergens = new HashSet<>();
    private Set<DishIngredientDTO> ingredients = new HashSet<>();
}