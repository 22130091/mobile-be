package com.client.mobile.module_order.service;

import com.client.mobile.module_order.dto.CartDTO;
import com.client.mobile.module_order.dto.CartItemDTO;

import java.util.List;

public interface CartService {
    CartDTO getCart(String sessionId);
    CartDTO addItemToCart(String sessionId, CartItemDTO cartItemDTO);
    CartDTO updateCartItem(String sessionId, Integer cartItemId, CartItemDTO cartItemDTO);
    CartDTO removeItemFromCart(String sessionId, Integer cartItemId);
    void clearCart(String sessionId);
    List<CartDTO> getAllCarts(); // Admin function
    void deleteExpiredCarts();
}