package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.*;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CartController {

    @DeleteMapping("/cart/{productId}")
    Mono<Cart> removeFromCart(@PathVariable int productId);

    @DeleteMapping("/cart")
    Mono<Cart> clearCart();

    @PostMapping("/cart")
    Mono<Order> submitCart();

    @PostMapping("/cart/{productId}")
    Mono<Cart> addToCart(@PathVariable int productId);

    @GetMapping("/cart")
    Flux<Product> getCartContent();
}
