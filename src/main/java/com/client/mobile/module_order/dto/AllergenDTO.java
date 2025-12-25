package com.client.mobile.module_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDTO {
    private Integer id;

    @NotBlank(message = "Allergen name is required")
    @Size(max = 50, message = "Allergen name must be less than 50 characters")
    private String name;

    private String description;
}