package com.client.mobile.module_order.controller;

import com.client.mobile.module_order.dto.IngredientDTO;
import com.client.mobile.module_order.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }

    @GetMapping("/search")
    public ResponseEntity<List<IngredientDTO>> searchIngredients(@RequestParam String name) {
        return ResponseEntity.ok(ingredientService.searchIngredientsByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredientService.getIngredientById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredient(@Valid @RequestBody IngredientDTO ingredientDTO) {
        return new ResponseEntity<>(ingredientService.createIngredient(ingredientDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(
            @PathVariable Integer id,
            @Valid @RequestBody IngredientDTO ingredientDTO) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, ingredientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}