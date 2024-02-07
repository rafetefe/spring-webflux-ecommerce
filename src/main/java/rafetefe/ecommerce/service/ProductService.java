package rafetefe.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.ProductsController;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductService implements ProductsController {

    ProductRepository productRepository;

    @Autowired //this annotation is not needed since this is the only constructor.
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product getProduct(int productId) {
        return productRepository.getById(productId);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.getAll();
    }

}
