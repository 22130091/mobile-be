package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.CartItemDTO;
import com.client.mobile.module_order.entity.CartItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "dish.id", target = "dishId")
    @Mapping(source = "dish.name", target = "dishName")
    @Mapping(source = "dish.imageUrl", target = "dishImageUrl")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(source = "dishId", target = "dish.id")
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "dish", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);

    List<CartItemDTO> toDTOList(List<CartItem> cartItems);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "dish", ignore = true)
    void updateEntityFromDTO(CartItemDTO cartItemDTO, @MappingTarget CartItem cartItem);
}

