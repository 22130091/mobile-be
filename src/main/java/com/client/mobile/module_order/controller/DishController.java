package com.client.mobile.module_order.controller;

import com.client.mobile.module_order.dto.DishDTO;
import com.client.mobile.module_order.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<DishDTO>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @GetMapping("/active")
    public ResponseEntity<List<DishDTO>> getActiveDishes() {
        return ResponseEntity.ok(dishService.getActiveDishes());
    }

    @GetMapping("/vegetarian")
    public ResponseEntity<List<DishDTO>> getVegetarianDishes() {
        return ResponseEntity.ok(dishService.getVegetarianDishes());
    }

    @GetMapping("/vegan")
    public ResponseEntity<List<DishDTO>> getVeganDishes() {
        return ResponseEntity.ok(dishService.getVeganDishes());
    }

    @GetMapping("/spicy")
    public ResponseEntity<List<DishDTO>> getSpicyDishes() {
        return ResponseEntity.ok(dishService.getSpicyDishes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DishDTO>> searchDishes(@RequestParam String name) {
        return ResponseEntity.ok(dishService.searchDishesByName(name));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<DishDTO>> getDishesByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(dishService.getDishesByCategory(categoryId));
    }

    @GetMapping("/by-price-range")
    public ResponseEntity<List<DishDTO>> getDishesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(dishService.getDishesByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/by-allergen/{allergenId}")
    public ResponseEntity<List<DishDTO>> getDishesByAllergen(@PathVariable Integer allergenId) {
        return ResponseEntity.ok(dishService.getDishesByAllergen(allergenId));
    }

    @GetMapping("/by-ingredient/{ingredientId}")
    public ResponseEntity<List<DishDTO>> getDishesByIngredient(@PathVariable Integer ingredientId) {
        return ResponseEntity.ok(dishService.getDishesByIngredient(ingredientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Integer id) {
        return ResponseEntity.ok(dishService.getDishById(id));
    }

    @PostMapping
    public ResponseEntity<DishDTO> createDish(@Valid @RequestBody DishDTO dishDTO) {
        return new ResponseEntity<>(dishService.createDish(dishDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDTO> updateDish(
            @PathVariable Integer id,
            @Valid @RequestBody DishDTO dishDTO) {
        return ResponseEntity.ok(dishService.updateDish(id, dishDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Integer id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateDish(@PathVariable Integer id) {
        dishService.activateDish(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDish(@PathVariable Integer id) {
        dishService.deactivateDish(id);
        return ResponseEntity.noContent().build();
    }
}
