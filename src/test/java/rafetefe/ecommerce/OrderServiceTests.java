package rafetefe.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.CartRepository;
import rafetefe.ecommerce.repository.OrderRepository;
import rafetefe.ecommerce.repository.ProductRepository;
import rafetefe.ecommerce.service.CartService;
import rafetefe.ecommerce.service.OrderService;
import rafetefe.ecommerce.service.UserSessionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class OrderServiceTests extends MongoDbTestContainer{

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    UserSessionService userSessionService;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup(){
        orderRepository.deleteAll().block();
        productRepository.deleteAll().block();
        cartRepository.deleteAll().block();
    }

    private Mono<Void> createOrder(){
        int ownerId = userSessionService.userId;
        int sampleProductId = 1;
        Product sample = new Product(sampleProductId, "productName"+sampleProductId, (double) sampleProductId);
        Mono<Product> productMono = productRepository.save(sample);

        return productMono.map(product -> {
            return cartService.addToCart(sampleProductId); //get and save to cart
        }).flatMap(e->e)
                .map(cart -> {
                    return cartService.submitCart();  //submit the cart
                }).flatMap(e->e).then();
    }

    @Test
    void getOngoing(){                      //expectComplete().verify() == verifyComplete()
        StepVerifier.create(orderService.getOngoingOrders()).expectNextCount(0).verifyComplete();
        createOrder().block();
        StepVerifier.create(orderService.getOngoingOrders()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getCancelled(){
        int userId = userSessionService.userId;

        //verify no order returns for empty repo
        StepVerifier.create(orderService.getCancelledOrders()).expectNextCount(0).verifyComplete();

        createOrder().block();

        //Verify upper call has created an order.
        StepVerifier.create(orderRepository.findAllByOwnerId(userId))
                .expectNextCount(1).verifyComplete();

        //test the service' getCancelled function.
        StepVerifier.create(orderService.getCancelledOrders())
                .expectNextCount(0).verifyComplete();

        //Cancel a order.
        orderRepository.findAllByOwnerId(userId).next().map(
                order -> {return orderService.cancelOrder(order.getOrderId());}
        ).flatMap(e->e).block();

        StepVerifier.create(orderService.getCancelledOrders())
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void getComplete(){
        int userId = userSessionService.userId;
        //verifies both the completion process and the obtaining of completed orders
        StepVerifier.create(orderService.getCompleteOrders())
                .expectNextCount(0).verifyComplete();

        createOrder().block();

        StepVerifier.create(orderRepository.findAllByOwnerId(userId))
                .expectNextCount(1)
                .verifyComplete();

        //set created order as complete
        orderRepository.findAllByOwnerId(userId).next()
                .map(order -> {
                    return orderService.completeOrder(order.getOrderId());
                }).flatMap(e->e).then().block();

        StepVerifier.create(orderService.getCompleteOrders())
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void apiGetOngoing(){
        List<Order> ongoingOrders = getListViaApiAndVerify("/order/ongoing");
        assertTrue(ongoingOrders.isEmpty());//verify returned list is empty

        createOrder().block();

        ongoingOrders = getListViaApiAndVerify("/order/ongoing");
        assertTrue(ongoingOrders.size() == 1);
    }

    @Test
    void apiGetCancelled(){
        int userId = userSessionService.userId;

        List<Order> cancelledOrders = getListViaApiAndVerify("/order/cancelled");
        assertTrue(cancelledOrders.isEmpty());

        createOrder().block();

        Order freshOrder = orderRepository.findAllByOwnerId(userId).next().block();

        //cancel order
        webClient.post().uri("/order/cancel/"+freshOrder.getOrderId())
                .exchange().expectStatus().is2xxSuccessful();

        //verify increment of cancelled order list.
        cancelledOrders = getListViaApiAndVerify("/order/cancelled");
        assertTrue(cancelledOrders.size() == 1);
    }

    @Test
    void apiGetCompleted(){
        int userId = userSessionService.userId;

        List<Order> completedOrders = getListViaApiAndVerify("/order/complete");
        assertTrue(completedOrders.isEmpty());

        createOrder().block();

        Order freshOrder = orderRepository.findAllByOwnerId(userId).next().block();

        webClient.post().uri("/order/complete/"+freshOrder.getOrderId())
                .exchange().expectStatus().is2xxSuccessful();

        completedOrders = getListViaApiAndVerify("/order/complete");
        assertTrue(completedOrders.size() == 1);
    }

    private List<Order> getListViaApiAndVerify(String uri){
        EntityExchangeResult<List<Order>> response = webClient.get().uri(uri).exchange()
                .expectBodyList(Order.class).returnResult();
        assertTrue(response.getStatus().is2xxSuccessful());
        return response.getResponseBody();
    }

}
