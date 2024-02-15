package rafetefe.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafetefe.ecommerce.domain.Product;

import java.util.List;


public interface ProductController {

    @PostMapping(value = "/product", produces = "application/json", consumes = "application/json")
    Product createProduct(@RequestBody Product body);

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    Product getProduct(@PathVariable int productId);

    @GetMapping("/products")
    List<Product> getAll();

    @DeleteMapping(value = "/product/{productId}")
    ResponseEntity deleteProduct(@PathVariable int productId);

    //function for testing Webclient
    @GetMapping("/webClientTest")
    String webClientTest();
}
