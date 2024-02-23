package rafetefe.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.CartRepository;
import rafetefe.ecommerce.repository.OrderRepository;
import rafetefe.ecommerce.repository.ProductRepository;
import rafetefe.ecommerce.service.CartService;
import rafetefe.ecommerce.service.UserSessionService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CartServiceTests extends MongoDbTestContainer{

    @Autowired
    CartService cartService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserSessionService userSessionService;
    @Autowired
    WebTestClient webClient;


    @BeforeEach
    void setUP(){
        StepVerifier.create(cartRepository.deleteAll()).verifyComplete();
        StepVerifier.create(orderRepository.deleteAll()).verifyComplete();
        StepVerifier.create(productRepository.deleteAll()).verifyComplete();
    }

    @Test
    void directAccessAdd(){
        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(0).verifyComplete();

        //create new product in system
        int dummyId = 6;
        createProductAndVerify(dummyId);

        //subject of test
        cartService.addToCart(dummyId).block();

        //recive added item for comperison purpose
        Product readProduct = productRepository.findByProductId(dummyId).block();

        StepVerifier.create(cartService.getCartContent())
                .expectNext(readProduct)
                .verifyComplete();
    }

    @Test
    void directAccessCreateAndClear(){
        int dummyId = 7;
        //creation of example product
        createProductAndVerify(dummyId);

        //creates or clears cart.
        //clear cart returns an empty cart object
        StepVerifier.create(cartService.clearCart()).expectNextCount(1).verifyComplete();


        StepVerifier.create(cartService.getCartContent()).expectNextCount(0).verifyComplete();

        //add product and verify
        StepVerifier.create(cartService.addToCart(dummyId)).expectNextCount(1).verifyComplete();

        StepVerifier.create(cartService.getCartContent())
                .expectNext(productRepository.findByProductId(dummyId).block())
                .verifyComplete();

        StepVerifier.create(cartService.clearCart()).expectNextCount(1).verifyComplete();

        StepVerifier.create(cartService.getCartContent()).expectNextCount(0).verifyComplete();
    }

    @Test
    void directAccessRemove(){
        int dummyId = 36; //new product just for the sake of better coverage
        createProductAndVerify(dummyId);

        //update and get the new cart
        StepVerifier.create(cartService.addToCart(dummyId)).expectNextCount(1).verifyComplete();

        //get cart content and assure it's made by only the this item.
        StepVerifier.create(cartService.getCartContent())
                .expectNext(productRepository.findByProductId(dummyId).block())
                .verifyComplete();

        //verify deletion works (will return updated cart)
        StepVerifier.create(cartService.removeFromCart(dummyId))
                .expectNextCount(1).verifyComplete();

        //expect it to be empty
        StepVerifier.create(cartService.getCartContent()).expectNextCount(0).verifyComplete();
    }

    @Test
    void directAccessSubmit(){
        int ownerId = userSessionService.userId;

        //post-@BeforeEach, assure order repo is empty.
        StepVerifier.create(orderRepository.findAllByOwnerId(ownerId))
                .expectNextCount(0).verifyComplete();

        //cart table is empty no order should be created.
        cartService.submitCart().block();
        //assure receiving empty list
        StepVerifier.create(orderRepository.findAllByOwnerId(ownerId))
                .expectNextCount(0).verifyComplete();

        //and empty cart is created, but order still shouldn't be created
        cartService.clearCart().block();
        cartService.submitCart().block();
        StepVerifier.create(orderRepository.findAllByOwnerId(ownerId))
                .expectNextCount(0).verifyComplete();

        int sampleProductId = 1;
        createProductAndVerify(sampleProductId);
        cartService.addToCart(sampleProductId).block();

        //expect order post submission
        StepVerifier.create(cartService.submitCart())
                .expectNextCount(1).verifyComplete();

        //assure that only 1 order is created
        StepVerifier.create(orderRepository.findAllByOwnerId(ownerId))
                .expectNextCount(1).verifyComplete();

        //confirm that current cart is cleared after submission
        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(0).verifyComplete();
    }

    @Test
    void apiAddProduct() {
        int ownerId = userSessionService.userId;
        int dummyId = 5;
        createProductAndVerify(dummyId);
        //verify existence of product in repo.

        //add the product to cart
        webClient.post().uri("/cart/" + dummyId).exchange()
                        .expectStatus().isOk();

        StepVerifier.create(cartService.getCartContent()).expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void apiGetCart(){
        //Add Product
        apiAddProduct();
        int sampleProductId = 5;

        //Get cartResponse
        EntityExchangeResult<List<Product>> result = webClient.get().uri("/cart").exchange()
                .expectBodyList(Product.class).returnResult();

        //assert response was 200
        assertTrue(result.getStatus().is2xxSuccessful());

        //parse the response into a list
        List<Product> cartContent = result.getResponseBody();

        StepVerifier.create(productRepository.findByProductId(sampleProductId))
                        .assertNext(product ->
                                product.contentWiseEqual(cartContent.get(0)))
                .verifyComplete();
    }

    @Test
    void apiRemoveFromCart(){
        /*
            @DeleteMapping("/cart/{productId}")
            void removeFromCart(@PathVariable int productId);
         */
        apiAddProduct();
        int sampleProductId = 5;

        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(1).verifyComplete();

        webClient.delete().uri("/cart/" + sampleProductId).exchange()
                    .expectStatus().is2xxSuccessful();

        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(0).verifyComplete();
    }

    @Test
    void apiClearCart(){
        apiAddProduct();

        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(1).verifyComplete();

        webClient.delete().uri("/cart").exchange()
                .expectStatus().is2xxSuccessful();

        StepVerifier.create(cartService.getCartContent())
                .expectNextCount(0).verifyComplete();
    }

    @Test
    void apiSubmitCart(){

        //State-of-no-Cart

        //attempt for submit 1
        webClient.post().uri("/cart").exchange()
                .expectStatus().isOk();


        //Assure no order is registered, when cart is not existent
        StepVerifier.create(orderRepository.findAll())
                .expectNextCount(0).verifyComplete();

        //Create Cart, empty cart shouldn't be submitted.
        apiClearCart();

        //attemp for submit 2
        webClient.post().uri("/cart").exchange().expectStatus().is2xxSuccessful();

        //Assure no order is registered, when cart is empty
        StepVerifier.create(orderRepository.findAll())
                .expectNextCount(0).verifyComplete();


        apiAddProduct();
        webClient.post().uri("/cart").exchange()
                .expectStatus().is2xxSuccessful();
        StepVerifier.create(orderRepository.findAll())
                .expectNextCount(1).verifyComplete();

    }



    public void createProductAndVerify(int dummyId){
        productRepository.deleteAll().block();
        Product sample = new Product(dummyId, "productName"+dummyId, (double) dummyId);
        productRepository.save(sample).block();
        StepVerifier.create(productRepository.findByProductId(dummyId))
                .expectNext(sample).verifyComplete();
    }
}
