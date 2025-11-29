package com.client.mobile.module_order.service;

import com.client.mobile.module_order.dto.IngredientDTO;

import java.util.List;

public interface IngredientService {
    List<IngredientDTO> getAllIngredients();
    IngredientDTO getIngredientById(Integer id);
    List<IngredientDTO> searchIngredientsByName(String name);
    IngredientDTO createIngredient(IngredientDTO ingredientDTO);
    IngredientDTO updateIngredient(Integer id, IngredientDTO ingredientDTO);
    void deleteIngredient(Integer id);
}