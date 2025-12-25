package com.client.mobile.module_order.service.impl;

import com.client.mobile.module_order.dto.CartDTO;
import com.client.mobile.module_order.dto.CartItemDTO;
import com.client.mobile.module_order.entity.Cart;
import com.client.mobile.module_order.entity.CartItem;
import com.client.mobile.module_order.entity.Dish;
import com.client.mobile.exception.ResourceNotFoundException;
import com.client.mobile.module_order.mapper.CartItemMapper;
import com.client.mobile.module_order.mapper.CartMapper;
import com.client.mobile.module_order.repository.CartItemRepository;
import com.client.mobile.module_order.repository.CartRepository;
import com.client.mobile.module_order.repository.DishRepository;
import com.client.mobile.module_order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final DishRepository dishRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartDTO getCart(String sessionId) {
        Cart cart = findOrCreateCart(sessionId);
        CartDTO cartDTO = cartMapper.toDTO(cart);
        // Load cart items
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        cartDTO.setItems(cartItemMapper.toDTOList(cartItems));
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(String sessionId, CartItemDTO cartItemDTO) {
        Cart cart = findOrCreateCart(sessionId);

        // Get the dish
        Dish dish = dishRepository.findById(cartItemDTO.getDishId())
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + cartItemDTO.getDishId()));

        // Check if the item is already in the cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndDishId(cart.getId(), dish.getId());

        if (existingItemOpt.isPresent()) {
            // Update quantity if item exists
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
            existingItem.setSpecialRequests(cartItemDTO.getSpecialRequests());
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setDish(dish);
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setSpecialRequests(cartItemDTO.getSpecialRequests());
            cartItem.setUnitPrice(dish.getPrice());
            cartItemRepository.save(cartItem);
        }

        // Refresh the cart and return
        return getCart(sessionId);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String sessionId, Integer cartItemId, CartItemDTO cartItemDTO) {
        Cart cart = findOrCreateCart(sessionId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify this item belongs to the cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to the specified cart");
        }

        // Update quantity and special requests
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItem.setSpecialRequests(cartItemDTO.getSpecialRequests());
        cartItemRepository.save(cartItem);

        // Refresh the cart and return
        return getCart(sessionId);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(String sessionId, Integer cartItemId) {
        Cart cart = findOrCreateCart(sessionId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify this item belongs to the cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to the specified cart");
        }

        // Remove the item
        cartItemRepository.delete(cartItem);

        // Refresh the cart and return
        return getCart(sessionId);
    }

    @Override
    @Transactional
    public void clearCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with session id: " + sessionId));

        // Delete all items in the cart
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cart.getId()));
    }

    @Override
    public List<CartDTO> getAllCarts() {
        return cartMapper.toDTOList(cartRepository.findAll());
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void deleteExpiredCarts() {
        // Delete carts that are older than 3 days
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(3);
        cartRepository.deleteByCreatedAtBefore(expirationDate);
    }

    private Cart findOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });
    }
}