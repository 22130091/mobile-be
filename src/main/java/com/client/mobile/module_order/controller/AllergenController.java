package com.client.mobile.module_order.controller;

import com.client.mobile.module_order.dto.AllergenDTO;
import com.client.mobile.module_order.service.AllergenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/allergens")
@RequiredArgsConstructor
public class AllergenController {

    private final AllergenService allergenService;

    @GetMapping
    public ResponseEntity<List<AllergenDTO>> getAllAllergens() {
        return ResponseEntity.ok(allergenService.getAllAllergens());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AllergenDTO>> searchAllergens(@RequestParam String name) {
        return ResponseEntity.ok(allergenService.searchAllergensByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllergenDTO> getAllergenById(@PathVariable Integer id) {
        return ResponseEntity.ok(allergenService.getAllergenById(id));
    }

    @PostMapping
    public ResponseEntity<AllergenDTO> createAllergen(@Valid @RequestBody AllergenDTO allergenDTO) {
        return new ResponseEntity<>(allergenService.createAllergen(allergenDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllergenDTO> updateAllergen(
            @PathVariable Integer id,
            @Valid @RequestBody AllergenDTO allergenDTO) {
        return ResponseEntity.ok(allergenService.updateAllergen(id, allergenDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllergen(@PathVariable Integer id) {
        allergenService.deleteAllergen(id);
        return ResponseEntity.noContent().build();
    }
}
