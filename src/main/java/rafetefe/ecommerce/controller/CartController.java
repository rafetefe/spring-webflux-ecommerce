package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.*;
import rafetefe.ecommerce.domain.Product;

import java.util.List;

public interface CartController {

    @DeleteMapping("/cart/{productId}")
    void removeFromCart(@PathVariable int productId);

    //wrap around for accidental requests?
    @DeleteMapping("/cart")
    void clearCart();

    @PostMapping("/cart")
    void submitCart();

    @PostMapping("/cart/{productId}")
    void addToCart(@PathVariable int productId);

    @GetMapping("/cart")
    List<Product> getCartContent();
}
