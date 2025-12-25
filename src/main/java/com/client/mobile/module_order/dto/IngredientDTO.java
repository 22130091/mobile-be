package com.client.mobile.module_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Integer id;

    @NotBlank(message = "Ingredient name is required")
    @Size(max = 100, message = "Ingredient name must be less than 100 characters")
    private String name;

    private String description;
}
