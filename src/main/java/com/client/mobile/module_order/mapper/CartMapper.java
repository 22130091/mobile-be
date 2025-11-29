package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.CartDTO;
import com.client.mobile.module_order.entity.Cart;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    CartDTO toDTO(Cart cart);

    Cart toEntity(CartDTO cartDTO);

    List<CartDTO> toDTOList(List<Cart> carts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(CartDTO cartDTO, @MappingTarget Cart cart);
}

