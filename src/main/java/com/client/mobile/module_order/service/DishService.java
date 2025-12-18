package com.client.mobile.module_order.service;

import com.client.mobile.module_order.dto.DishDTO;

import java.math.BigDecimal;
import java.util.List;

public interface DishService {
    List<DishDTO> getAllDishes();
    DishDTO getDishById(Integer id);
    List<DishDTO> searchDishesByName(String name);
    List<DishDTO> getDishesByCategory(Integer categoryId);
    List<DishDTO> getVegetarianDishes();
    List<DishDTO> getVeganDishes();
    List<DishDTO> getSpicyDishes();
    List<DishDTO> getActiveDishes();
    List<DishDTO> getDishesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<DishDTO> getDishesByAllergen(Integer allergenId);
    List<DishDTO> getDishesByIngredient(Integer ingredientId);
    DishDTO createDish(DishDTO dishDTO);
    DishDTO updateDish(Integer id, DishDTO dishDTO);
    void deleteDish(Integer id);
    void activateDish(Integer id);
    void deactivateDish(Integer id);
}
