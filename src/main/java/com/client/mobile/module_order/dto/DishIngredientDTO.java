package com.client.mobile.module_order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredientDTO {
    @NotNull
    private Integer ingredientId;
    private String ingredientName;
    private String quantity;
}