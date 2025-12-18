package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.AllergenDTO;
import com.client.mobile.module_order.entity.Allergen;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.AllergenMapper;
import com.client.mobile.module_order.repository.AllergenRepository;
import com.client.mobile.module_order.service.AllergenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergenServiceImpl implements AllergenService {

    private final AllergenRepository allergenRepository;
    private final AllergenMapper allergenMapper;

    @Override
    public List<AllergenDTO> getAllAllergens() {
        return allergenMapper.toDTOList(allergenRepository.findAll());
    }

    @Override
    public AllergenDTO getAllergenById(Integer id) {
        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with id: " + id));
        return allergenMapper.toDTO(allergen);
    }

    @Override
    public List<AllergenDTO> searchAllergensByName(String name) {
        return allergenMapper.toDTOList(allergenRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public AllergenDTO createAllergen(AllergenDTO allergenDTO) {
        Allergen allergen = allergenMapper.toEntity(allergenDTO);
        Allergen savedAllergen = allergenRepository.save(allergen);
        return allergenMapper.toDTO(savedAllergen);
    }

    @Override
    public AllergenDTO updateAllergen(Integer id, AllergenDTO allergenDTO) {
        Allergen existingAllergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with id: " + id));

        allergenMapper.updateEntityFromDTO(allergenDTO, existingAllergen);
        Allergen updatedAllergen = allergenRepository.save(existingAllergen);
        return allergenMapper.toDTO(updatedAllergen);
    }

    @Override
    public void deleteAllergen(Integer id) {
        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allergen not found with id: " + id));
        allergenRepository.delete(allergen);
    }
}