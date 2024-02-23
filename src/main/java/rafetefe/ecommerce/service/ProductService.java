package rafetefe.ecommerce.service;

import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.ProductController;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.logging.Level.FINE;

@RestController
public class ProductService implements ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Mono<Product> createProduct(Product body) {
        return productRepository.save(body)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex->new Exception("Product with same ID already exists."+body.getProductId())
                );//drop creation if product already exists.
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        return productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new Exception("getProduct: no product found with given id"+productId)))
                .log(LOG.getName(), FINE)
                .onErrorMap(ex -> new Exception("Error on getProduct"+ex.getMessage()));
    }

    @Override
    public Flux<Product> getAll() {
        return productRepository.findAll()
                .log(LOG.getName(), FINE)
                .onErrorMap(ex -> new Exception("Error on getAll:"+ex.getMessage()));
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {

        return productRepository.deleteByProductId(productId)
                .log(LOG.getName(), FINE)
                .onErrorMap(ex -> new Exception("LOG Error on deleteByProductId:"+ex.getMessage()));

        //idempotent function
    }

    @Override
    public Mono<String> webClientTest(){
        return Mono.just("hello");
    }

}
