package rafetefe.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;
import rafetefe.ecommerce.service.ProductService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/*
* TODO: TESTLERİ reactive yap.
* */

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductServiceTests extends MongoDbTestContainer{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup(){
        //Clear testContainer
        //productRepository.deleteAll();
        productRepository.deleteAll().block();
    }

    @Test
    void insertionByRepo(){
        Product sample = new Product(37, "ExampleProduct", 11.2);

        productRepository.save(sample).block();

        //from: reactor-test
        StepVerifier.create(productRepository.findByProductId(37))
                .expectNextMatches(queryResult -> queryResult.contentWiseEqual(sample))
                .verifyComplete();
    }

    @Test
    void serviceDirectAccessTest(){
        int sampleId=10;
        Product sample = new Product(sampleId,"name",5.0);
        productService.createProduct(sample).block();

//        assertTrue(productService.getProduct(sampleId).block().equals(sample));
        StepVerifier.create(productService.getProduct(sampleId))
                .expectNext(sample)
                .verifyComplete();

        int sampleId2=3;
        Product sample2 = new Product(sampleId2, "name", 3.0);
        productService.createProduct(sample2).block();

        StepVerifier.create(productService.getAll())
                .expectNext(sample, sample2)
                .verifyComplete();

        productService.deleteProduct(sampleId).block();

        StepVerifier.create(productRepository.findByProductId(sampleId))
                .verifyComplete();

        productService.deleteProduct(sampleId2).block();

        //List is empty error
        StepVerifier.create(productService.getAll()).expectError();

    }

    @Test
    void insertionByApi(){
        productRepository.deleteAll().block();

        Product sample = new Product(12, "exampleProduct", 6.4);

        webClient.post().uri("/product")
                .bodyValue(sample)
                .exchange();

        StepVerifier.create(productRepository.findByProductId(12))
                .assertNext(product -> product.contentWiseEqual(sample)).verifyComplete();
    }

    @Test
    void getByApi(){
        //add using webclient and get http.Ok
        deleteAllInsertAndVerify(12);

        //check the repo for record existence
        StepVerifier.create(productRepository.findByProductId(12)).expectNextCount(1).verifyComplete();

        //obtain the record using api
        Product got = webClient.get().uri("/product/"+12).
                exchange().expectBody(Product.class).returnResult().getResponseBody();

        //obtain the record from repo
        Mono<Product> query = productRepository.findByProductId(12);

        StepVerifier.create(query).expectNext(got).verifyComplete();
    }

    @Test
    void getAllByApi(){
        productRepository.deleteAll().block();

        EntityExchangeResult<List<Product>> res = webClient.get()
                .uri("/products")
                .exchange()
                .expectBodyList(Product.class)
                .returnResult();

        assertTrue(res.getStatus().is2xxSuccessful());

        List<Product> resBody = res.getResponseBody();
        assertTrue(resBody.size() == 0);

        //Redo the above after adding a product.
        insertionByApi();

        res = webClient.get()
                .uri("/products")
                .exchange()
                .expectBodyList(Product.class)
                .returnResult();

        assertTrue(res.getStatus().is2xxSuccessful());

        resBody = res.getResponseBody();
        assertTrue(resBody.size() == 1);

    }

    @Test
    void deletionByApi(){
        int sampleId = 13;
        assertTrue(deleteAllInsertAndVerify(sampleId));

        StepVerifier.create(productRepository.findByProductId(sampleId))
                .expectNextCount(1).verifyComplete(); //expect 1 item

        //delete product
        webClient.delete().uri("/product/"+sampleId).exchange()
                        .expectStatus().isOk();

        //assert delation of product.
        StepVerifier.create(productRepository.findByProductId(sampleId))
                .expectNextCount(0).verifyComplete();


        webClient.delete().uri("/product/"+sampleId).exchange()
                .expectStatus().isOk();
    }

    private boolean deleteAllInsertAndVerify(int productId){
        productRepository.deleteAll().block();

        Product sample = new Product(productId, "exampleProduct", 6.4);

        return webClient.post().uri("/product")
                .bodyValue(sample)
                .exchange()
                .expectBody(String.class)
                .returnResult().getStatus().equals(HttpStatus.OK);
    }
}
