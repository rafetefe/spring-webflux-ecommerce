package rafetefe.ecommerce;

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

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductServiceTests extends MongoDbTestContainer{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @Autowired
    WebTestClient webClient;

    @Test
    void insertionByRepo(){
        /*
        / Confirms:
            repo.Save
            repo.findByProductId
            product.equals
         */

        //Clear testContainer
        productRepository.deleteAll();

        Product sample = new Product(37, "ExampleProduct", 11.2);

        productRepository.save(sample);

        Product queriedProduct = productRepository.findByProductId(37).get();

        assertTrue(queriedProduct.equals(sample));
    }

    @Test
    void serviceDirectAccessTest(){
        productRepository.deleteAll();
        int sampleId=10;
        Product sample = new Product(sampleId,"name",5.0);
        productService.createProduct(sample);
        assertTrue(productRepository.findByProductId(sampleId).isPresent());
        assertTrue(productService.getProduct(sampleId).equals(sample));

        int sampleId2=3;
        Product sample2 = new Product(sampleId2, "name", 3.0);
        productService.createProduct(sample2);

        List<Product> list = productService.getAll();
        assertTrue(list.size() == 2);
        assertTrue(list.get(0).equals(sample));
        assertTrue(list.get(1).equals(sample2));

        productService.deleteProduct(sampleId);
        assertTrue(productRepository.findByProductId(sampleId).isEmpty());

        productService.deleteProduct(sampleId2);
        list = productService.getAll();

        assertTrue(list.size() == 0);
    }

    @Test
    void insertionByApi(){

        /*
         * Confirms:
         * POST on /product successfully parses
         *
         * This was unsuccessful for reason of Product class not having
         * get and set id methods. lessons learned.
         * */

        productRepository.deleteAll();

        Product sample = new Product(12, "exampleProduct", 6.4);

        webClient.post().uri("/product")
                .bodyValue(sample)
                .exchange();

        Product queriedProduct = productRepository.findByProductId(12).get();

        assertTrue(queriedProduct.contentWiseEqual(sample));

    }

    @Test
    void getByApi(){
        //add using webclient and get http.Ok
        assertTrue(deleteAllInsertAndVerify(12));

        //check the repo for record existence
        assertTrue(productRepository.findByProductId(12).isPresent());

        //obtain the record using api
        Product got = webClient.get().uri("/product/"+12).
                exchange().expectBody(Product.class).returnResult().getResponseBody();

        //obtain the record from repo
        Product query = productRepository.findByProductId(12).get();

        //assert their equality
        assertTrue(got.equals(query));
    }

    @Test
    void getAllByApi(){
        productRepository.deleteAll();;

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
        assertTrue(productRepository.findByProductId(sampleId).isPresent());

        assertTrue(
                webClient.delete().uri("/product/"+sampleId)
                        .exchange()
                        .returnResult(ResponseEntity.class)
                        .getStatus().equals(HttpStatus.ACCEPTED)
        );

        assertTrue(productRepository.findByProductId(sampleId).isEmpty());

        //verify HTTPStatus NotFound
        assertTrue(
                webClient.delete().uri("/product/"+sampleId)
                        .exchange()
                        .returnResult(ResponseEntity.class)
                        .getStatus().equals(HttpStatus.NOT_FOUND)
        );

    }

    private boolean deleteAllInsertAndVerify(int productId){
        productRepository.deleteAll();

        Product sample = new Product(productId, "exampleProduct", 6.4);

        return webClient.post().uri("/product")
                .bodyValue(sample)
                .exchange()
                .expectBody(String.class)
                .returnResult().getStatus().equals(HttpStatus.OK);
    }
}
