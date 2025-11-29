package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.DishDTO;
import com.client.mobile.module_order.entity.Dish;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AllergenMapper.class, IngredientMapper.class})
public interface DishMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    DishDTO toDTO(Dish dish);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "orderItems", ignore = true)
    Dish toEntity(DishDTO dishDTO);

    List<DishDTO> toDTOList(List<Dish> dishes);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateEntityFromDTO(DishDTO dishDTO, @MappingTarget Dish dish);
}

