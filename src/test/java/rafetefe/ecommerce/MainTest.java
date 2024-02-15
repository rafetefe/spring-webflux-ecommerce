package rafetefe.ecommerce;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MainTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void webClientTest(){
        assertTrue(
                (new String(webClient.get().uri("/webClientTest").exchange().
                expectBody().returnResult().getResponseBody()))
                        .equals("hello")
        );
    }

    @Test
    void deneme(){

    }
}
