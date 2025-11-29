package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.DishDTO;
import com.client.mobile.module_order.dto.DishIngredientDTO;
import com.client.mobile.module_order.entity.*;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.DishMapper;
import com.client.mobile.module_order.repository.*;
import com.client.mobile.module_order.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final AllergenRepository allergenRepository;
    private final IngredientRepository ingredientRepository;
    private final DishIngredientRepository dishIngredientRepository;
    private final DishMapper dishMapper;

    @Override
    public List<DishDTO> getAllDishes() {
        return dishMapper.toDTOList(dishRepository.findAll());
    }

    @Override
    public DishDTO getDishById(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
        return dishMapper.toDTO(dish);
    }

    @Override
    public List<DishDTO> searchDishesByName(String name) {
        return dishMapper.toDTOList(dishRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public List<DishDTO> getDishesByCategory(Integer categoryId) {
        return dishMapper.toDTOList(dishRepository.findByCategoryId(categoryId));
    }

    @Override
    public List<DishDTO> getVegetarianDishes() {
        return dishMapper.toDTOList(dishRepository.findByIsVegetarian(true));
    }

    @Override
    public List<DishDTO> getVeganDishes() {
        return dishMapper.toDTOList(dishRepository.findByIsVegan(true));
    }

    @Override
    public List<DishDTO> getSpicyDishes() {
        return dishMapper.toDTOList(dishRepository.findByIsSpicy(true));
    }

    @Override
    public List<DishDTO> getActiveDishes() {
        return dishMapper.toDTOList(dishRepository.findByActive(true));
    }

    @Override
    public List<DishDTO> getDishesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return dishMapper.toDTOList(dishRepository.findByPriceRange(minPrice, maxPrice));
    }

    @Override
    public List<DishDTO> getDishesByAllergen(Integer allergenId) {
        return dishMapper.toDTOList(dishRepository.findByAllergenId(allergenId));
    }

    @Override
    public List<DishDTO> getDishesByIngredient(Integer ingredientId) {
        return dishMapper.toDTOList(dishRepository.findByIngredientId(ingredientId));
    }

    @Override
    @Transactional
    public DishDTO createDish(DishDTO dishDTO) {
        Dish dish = dishMapper.toEntity(dishDTO);

        // Set category
        if (dishDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(dishDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dishDTO.getCategoryId()));
            dish.setCategory(category);
        }

        // Save dish first to get ID
        Dish savedDish = dishRepository.save(dish);

        // Set allergens
        if (dishDTO.getAllergens() != null && !dishDTO.getAllergens().isEmpty()) {
            Set<Allergen> allergens = new HashSet<>();
            dishDTO.getAllergens().forEach(allergenDTO -> {
                Allergen allergen = allergenRepository.findById(allergenDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with id: " + allergenDTO.getId()));
                allergens.add(allergen);
            });
            savedDish.setAllergens(allergens);
        }

        // Save the dish with allergens
        savedDish = dishRepository.save(savedDish);

        // Handle ingredients and their quantities
        if (dishDTO.getIngredients() != null && !dishDTO.getIngredients().isEmpty()) {
            for (DishIngredientDTO diDTO : dishDTO.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(diDTO.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + diDTO.getIngredientId()));

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setDish(savedDish);
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setQuantity(diDTO.getQuantity());

                dishIngredientRepository.save(dishIngredient);
            }
        }

        // Reload the dish with all relationships
        return dishMapper.toDTO(dishRepository.findById(savedDish.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public DishDTO updateDish(Integer id, DishDTO dishDTO) {
        Dish existingDish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));

        dishMapper.updateEntityFromDTO(dishDTO, existingDish);

        // Update category
        if (dishDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(dishDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dishDTO.getCategoryId()));
            existingDish.setCategory(category);
        }

        // Update allergens
        if (dishDTO.getAllergens() != null) {
            existingDish.getAllergens().clear();
            dishDTO.getAllergens().forEach(allergenDTO -> {
                Allergen allergen = allergenRepository.findById(allergenDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with id: " + allergenDTO.getId()));
                existingDish.getAllergens().add(allergen);
            });
        }

        // Save the dish with updated properties and allergens
        Dish updatedDish = dishRepository.save(existingDish);

        // Update ingredients
        if (dishDTO.getIngredients() != null) {
            // Remove existing dish-ingredient relationships
            List<DishIngredient> existingDishIngredients = dishIngredientRepository.findByDishId(id);
            dishIngredientRepository.deleteAll(existingDishIngredients);

            // Add new dish-ingredient relationships
            for (DishIngredientDTO diDTO : dishDTO.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(diDTO.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + diDTO.getIngredientId()));

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setDish(updatedDish);
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setQuantity(diDTO.getQuantity());

                dishIngredientRepository.save(dishIngredient);
            }
        }

        // Reload the dish with all relationships
        return dishMapper.toDTO(dishRepository.findById(updatedDish.getId()).orElseThrow());
    }

    @Override
    public void deleteDish(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
        dishRepository.delete(dish);
    }

    @Override
    public void activateDish(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
        dish.setActive(true);
        dishRepository.save(dish);
    }

    @Override
    public void deactivateDish(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
        dish.setActive(false);
        dishRepository.save(dish);
    }
}

