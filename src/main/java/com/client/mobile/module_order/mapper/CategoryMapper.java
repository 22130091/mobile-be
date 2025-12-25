package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.CategoryDTO;
import com.client.mobile.module_order.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    @Mapping(target = "dishes", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    List<CategoryDTO> toDTOList(List<Category> categories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dishes", ignore = true)
    void updateEntityFromDTO(CategoryDTO categoryDTO, @MappingTarget Category category);
}

