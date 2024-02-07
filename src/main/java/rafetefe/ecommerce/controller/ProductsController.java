package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import rafetefe.ecommerce.domain.Product;

import java.util.List;


@RequestMapping("/product")
public interface ProductsController {

    @GetMapping(value = "/{productId}", produces = "application/json")
    Product getProduct(@PathVariable int productId);

    @GetMapping("/")
    List<Product> getAll();
}
