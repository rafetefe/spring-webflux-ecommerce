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
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void directAccessAdd(){
        //assure user cart is empty
        assertTrue(cartService.getCartContent().size() == 0);

        //create new product in system
        int dummyId = 6;
        assertTrue(createProductAndVerify(dummyId));

        cartService.addToCart(dummyId);

        //verify product is added and the cart grew by 1.
        Product p = productRepository.findByProductId(dummyId).get();
        assertTrue(cartService.getCartContent().contains(p));
        assertTrue(cartService.getCartContent().size() == 1);
    }

    @Test
    void directAccessCreateAndClear(){

        int dummyId = 7;
        assertTrue(createProductAndVerify(dummyId));

        //creates or clears cart.
        cartService.clearCart();
        assertTrue(cartService.getCartContent().size() == 0);

        //add product and verify
        cartService.addToCart(dummyId);
        assertTrue(cartService.getCartContent().size() == 1);
        assertTrue(cartService.getCartContent().get(0).equals(productRepository.findByProductId(dummyId).get()));

        //verify clearCart clears a non-empty cart.
        //this test caused me to notice an algorithm-flaw/bug.
        cartService.clearCart();
        assertTrue(cartService.getCartContent().size() == 0);
    }

    @Test
    void directAccessRemove(){

        int dummyId = 36; //new product just for the sake of better coverage
        assertTrue(createProductAndVerify(dummyId));

        cartService.addToCart(dummyId);
        assertTrue(cartService.getCartContent().get(0).equals(productRepository.findByProductId(dummyId).get()));
        assertTrue(cartService.getCartContent().size() == 1);

        //verify deletion works
        cartService.removeFromCart(dummyId);
        assertTrue(cartService.getCartContent().size() == 0);
    }

    @Test
    void directAccessSubmit(){
        int ownerId = userSessionService.userId;

        //assure order table is clear.
        assertTrue(orderRepository.findAllByOwnerId(ownerId).size() == 0);

        //cart table is empty no order should be created.
        cartService.submitCart();
        assertTrue(orderRepository.findAllByOwnerId(ownerId).size() == 0);

        //and empty cart is created, but order still shouldn't be created
        cartService.clearCart();
        cartService.submitCart();
        assertTrue(orderRepository.findAllByOwnerId(ownerId).size() == 0);

        int sampleProductId = 1;
        createProductAndVerify(sampleProductId);
        cartService.addToCart(sampleProductId);
        cartService.submitCart();

        //assure that only 1 order is created
        assertTrue(orderRepository.findAllByOwnerId(ownerId).size() == 1);

        //confirm that current cart is cleared after submission
        assertTrue(cartService.getCartContent().size()==0);
    }

        /*
            TestList:

            @DeleteMapping("/cart/{productId}")
            void removeFromCart(@PathVariable int productId);

            @DeleteMapping("/cart")
            void clearCart();

            @PostMapping("/cart")
            void submitCart();

            @PostMapping("/cart/{productId}")
            void addToCart(@PathVariable int productId);

            @GetMapping("/cart")
            List<Product> getCartContent();
     */

    //Calling the functions through apis. Extensive business logics
    //already is tested at above.
    @Test
    void apiAddProduct() {
        int ownerId = userSessionService.userId;
        int sampleProductId = 5;
        createProductAndVerify(sampleProductId);

        //add the product to cart
        assertTrue(
                webClient.post().uri("/cart/" + sampleProductId).exchange()
                        .returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );

        assertTrue(cartService.getCartContent().contains(
                productRepository.findByProductId(sampleProductId).get()
        ));
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

        //assert that list contains the added product from above in the first api call.
        Product sampleProduct = productRepository.findByProductId(sampleProductId).get();

        //equals fails ??
        assertTrue(cartContent.get(0).contentWiseEqual(sampleProduct));
        assertTrue(cartContent.size() == 1);
    }

    @Test
    void apiRemoveFromCart(){
        /*
            @DeleteMapping("/cart/{productId}")
            void removeFromCart(@PathVariable int productId);
         */
        apiAddProduct();
        int sampleProductId = 5;

        assertTrue(cartService.getCartContent().size() == 1);

        assertTrue(
                webClient.delete().uri("/cart/" + sampleProductId).exchange()
                        .returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );

        assertTrue(cartService.getCartContent().size() == 0);
    }

    @Test
    void apiClearCart(){
        apiAddProduct();

        assertTrue(cartService.getCartContent().size() == 1);

        assertTrue(
                webClient.delete().uri("/cart").exchange().returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );

        assertTrue(cartService.getCartContent().size() == 0);

    }

    @Test
    void apiSubmitCart(){

        //No Cart
        assertTrue(
                webClient.post().uri("/cart").exchange()
                        .returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );

        //Assure no order is registered, when cart is not existent
        assertTrue(
                StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                        .toList().isEmpty()
        );

        //Create Cart, empty cart shouldn't be submitted.
        apiClearCart();

        assertTrue(
                webClient.post().uri("/cart").exchange()
                        .returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );
        //Assure no order is registered, when cart is empty
        assertTrue(
                StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                        .toList().isEmpty()
        );

        apiAddProduct();
        assertTrue(
                webClient.post().uri("/cart").exchange()
                        .returnResult(WebTestClient.ResponseSpec.class)
                        .getStatus().is2xxSuccessful()
        );
        assertTrue(
                StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                        .toList().size() == 1
        );
    }



    public boolean createProductAndVerify(int dummyId){
        productRepository.deleteAll();
        Product sample = new Product(dummyId, "productName"+dummyId, (double) dummyId);
        productRepository.save(sample);
        return productRepository.findByProductId(dummyId).isPresent();
    }
}
