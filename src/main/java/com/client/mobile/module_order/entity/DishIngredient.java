package com.client.mobile.module_order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "dish_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DishIngredientId.class)
public class DishIngredient {

    @Id
    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(length = 50)
    private String quantity;
}