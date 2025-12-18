package com.client.mobile.module_order.controller;

import com.client.mobile.module_order.dto.CartDTO;
import com.client.mobile.module_order.dto.CartItemDTO;
import com.client.mobile.module_order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable String sessionId) {
        return ResponseEntity.ok(cartService.getCart(sessionId));
    }

    @PostMapping("/{sessionId}/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable String sessionId,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(sessionId, cartItemDTO));
    }

    @PutMapping("/{sessionId}/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable String sessionId,
            @PathVariable Integer itemId,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.ok(cartService.updateCartItem(sessionId, itemId, cartItemDTO));
    }

    @DeleteMapping("/{sessionId}/items/{itemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @PathVariable String sessionId,
            @PathVariable Integer itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(sessionId, itemId));
    }

    @DeleteMapping("/{sessionId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build();
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @DeleteMapping("/admin/expired")
    public ResponseEntity<Void> deleteExpiredCarts() {
        cartService.deleteExpiredCarts();
        return ResponseEntity.noContent().build();
    }
}