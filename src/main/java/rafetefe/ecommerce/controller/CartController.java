package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/cart")
public interface CartController {

    @PostMapping("/remove/{id}")
    void removeFromCart(@PathVariable int productID);

    //wrap around for accidental requests?
    @PostMapping("/clear")
    void clearCart();

    @PostMapping("/submit")
    void submitCart();

    @PostMapping("/add/{productId}")
    void addToCart(@PathVariable int productId);
}
