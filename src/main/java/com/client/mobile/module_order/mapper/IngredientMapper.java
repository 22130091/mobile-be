package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.IngredientDTO;
import com.client.mobile.module_order.entity.Ingredient;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    IngredientDTO toDTO(Ingredient ingredient);

    @Mapping(target = "dishes", ignore = true)
    Ingredient toEntity(IngredientDTO ingredientDTO);

    List<IngredientDTO> toDTOList(List<Ingredient> ingredients);

    Set<IngredientDTO> toDTOSet(Set<Ingredient> ingredients);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dishes", ignore = true)
    void updateEntityFromDTO(IngredientDTO ingredientDTO, @MappingTarget Ingredient ingredient);
}

