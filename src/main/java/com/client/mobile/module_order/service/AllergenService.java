package com.client.mobile.module_order.service;

import com.client.mobile.module_order.dto.AllergenDTO;

import java.util.List;

public interface AllergenService {
    List<AllergenDTO> getAllAllergens();
    AllergenDTO getAllergenById(Integer id);
    List<AllergenDTO> searchAllergensByName(String name);
    AllergenDTO createAllergen(AllergenDTO allergenDTO);
    AllergenDTO updateAllergen(Integer id, AllergenDTO allergenDTO);
    void deleteAllergen(Integer id);
}