package com.client.mobile.module_order.mapper;

import com.client.mobile.module_order.dto.OrderItemDTO;
import com.client.mobile.module_order.entity.OrderItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "dish.id", target = "dishId")
    @Mapping(source = "dish.name", target = "dishName")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(source = "dishId", target = "dish.id")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dish", ignore = true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);

    List<OrderItemDTO> toDTOList(List<OrderItem> orderItems);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "dish", ignore = true)
    void updateEntityFromDTO(OrderItemDTO orderItemDTO, @MappingTarget OrderItem orderItem);
}

