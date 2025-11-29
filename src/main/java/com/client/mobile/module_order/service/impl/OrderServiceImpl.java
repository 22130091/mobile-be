package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.OrderDTO;
import com.client.mobile.module_order.dto.OrderItemDTO;
import com.client.mobile.module_order.entity.Dish;
import com.client.mobile.module_order.entity.Order;
import com.client.mobile.module_order.entity.OrderItem;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.OrderItemMapper;
import com.client.mobile.module_order.mapper.OrderMapper;
import com.client.mobile.module_order.repository.DishRepository;
import com.client.mobile.module_order.repository.OrderItemRepository;
import com.client.mobile.module_order.repository.OrderRepository;
import com.client.mobile.module_order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderMapper.toDTOList(orderRepository.findAll());
    }

    @Override
    public OrderDTO getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(Integer customerId) {
        return orderMapper.toDTOList(orderRepository.findByCustomerId(customerId));
    }

    @Override
    public List<OrderDTO> getOrdersByReservationId(Integer reservationId) {
        return orderMapper.toDTOList(orderRepository.findByReservationId(reservationId));
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderMapper.toDTOList(orderRepository.findByStatus(status));
    }

    @Override
    public List<OrderDTO> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orderMapper.toDTOList(orderRepository.findByPaymentStatus(paymentStatus));
    }

    @Override
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderMapper.toDTOList(orderRepository.findByOrderTimeBetween(start, end));
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setReservationId(orderDTO.getReservationId());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setOrderTime(LocalDateTime.now());
        order.setNotes(orderDTO.getNotes());
        order.setStatus(Order.OrderStatus.pending);
        order.setPaymentStatus(Order.PaymentStatus.unpaid);
        order.setPaymentMethod(orderDTO.getPaymentMethod());

        // Save the order first to get ID
        Order savedOrder = orderRepository.save(order);

        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Dish dish = dishRepository.findById(itemDTO.getDishId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + itemDTO.getDishId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setDish(dish);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(dish.getPrice());
            orderItem.setSpecialRequests(itemDTO.getSpecialRequests());
            orderItem.setStatus(OrderItem.OrderItemStatus.pending);

            orderItems.add(orderItem);

            // Calculate total
            BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // Save all order items
        orderItemRepository.saveAll(orderItems);

        // Update total amount
        savedOrder.setTotalAmount(totalAmount);
        Order finalOrder = orderRepository.save(savedOrder);

        return orderMapper.toDTO(finalOrder);
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Integer id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        existingOrder.setReservationId(orderDTO.getReservationId());
        existingOrder.setCustomerId(orderDTO.getCustomerId());
        existingOrder.setNotes(orderDTO.getNotes());
        existingOrder.setPaymentMethod(orderDTO.getPaymentMethod());

        // Handle status updates if provided
        if (orderDTO.getStatus() != null) {
            existingOrder.setStatus(orderDTO.getStatus());
        }

        if (orderDTO.getPaymentStatus() != null) {
            existingOrder.setPaymentStatus(orderDTO.getPaymentStatus());
        }

        // Update order items if provided
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            // Remove existing items
            orderItemRepository.deleteAll(orderItemRepository.findByOrderId(id));

            // Add new items
            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItemDTO itemDTO : orderDTO.getItems()) {
                Dish dish = dishRepository.findById(itemDTO.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + itemDTO.getDishId()));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(existingOrder);
                orderItem.setDish(dish);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setUnitPrice(dish.getPrice());
                orderItem.setSpecialRequests(itemDTO.getSpecialRequests());

                // Use existing status if provided, otherwise set to PENDING
                orderItem.setStatus(itemDTO.getStatus() != null ? itemDTO.getStatus() : OrderItem.OrderItemStatus.pending);

                orderItems.add(orderItem);

                // Calculate total
                BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }

            // Save all order items
            orderItemRepository.saveAll(orderItems);

            // Update total amount
            existingOrder.setTotalAmount(totalAmount);
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public OrderDTO updateOrderStatus(Integer id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(status);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public OrderDTO updatePaymentStatus(Integer id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setPaymentStatus(paymentStatus);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }
}
