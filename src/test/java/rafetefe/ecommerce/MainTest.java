package rafetefe.ecommerce;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
public class MainTest {

    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    @Autowired
    private ProductRepository productRepository;

    @Test
    void insertProduct(){
        productRepository.insertProduct(new Product());
        productRepository.insertProduct(new Product());
        LOG.info("Product Insertion Complete");
    }

}
