package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.IngredientDTO;
import com.client.mobile.module_order.entity.Ingredient;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.IngredientMapper;
import com.client.mobile.module_order.repository.IngredientRepository;
import com.client.mobile.module_order.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    public List<IngredientDTO> getAllIngredients() {
        return ingredientMapper.toDTOList(ingredientRepository.findAll());
    }

    @Override
    public IngredientDTO getIngredientById(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
        return ingredientMapper.toDTO(ingredient);
    }

    @Override
    public List<IngredientDTO> searchIngredientsByName(String name) {
        return ingredientMapper.toDTOList(ingredientRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public IngredientDTO createIngredient(IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return ingredientMapper.toDTO(savedIngredient);
    }

    @Override
    public IngredientDTO updateIngredient(Integer id, IngredientDTO ingredientDTO) {
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));

        ingredientMapper.updateEntityFromDTO(ingredientDTO, existingIngredient);
        Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);
        return ingredientMapper.toDTO(updatedIngredient);
    }

    @Override
    public void deleteIngredient(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
        ingredientRepository.delete(ingredient);
    }
}
