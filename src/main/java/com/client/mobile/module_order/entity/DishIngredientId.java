package com.client.mobile.module_order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredientId implements Serializable {
    private Integer dish;
    private Integer ingredient;
}