package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.CategoryDTO;
import com.client.mobile.module_order.entity.Category;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.CategoryMapper;
import com.client.mobile.module_order.repository.CategoryRepository;
import com.client.mobile.module_order.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.toDTOList(categoryRepository.findAll());
    }

    @Override
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    @Override
    public List<CategoryDTO> searchCategoriesByName(String name) {
        return categoryMapper.toDTOList(categoryRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public List<CategoryDTO> getActiveCategories() {
        return categoryMapper.toDTOList(categoryRepository.findByActive(true));
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryMapper.updateEntityFromDTO(categoryDTO, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
