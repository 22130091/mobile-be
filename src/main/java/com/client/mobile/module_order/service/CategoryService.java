package com.client.mobile.module_order.service;

import com.client.mobile.module_order.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Integer id);
    List<CategoryDTO> searchCategoriesByName(String name);
    List<CategoryDTO> getActiveCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO);
    void deleteCategory(Integer id);
}