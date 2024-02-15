package rafetefe.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.CartRepository;
import rafetefe.ecommerce.repository.OrderRepository;
import rafetefe.ecommerce.repository.ProductRepository;
import rafetefe.ecommerce.service.CartService;
import rafetefe.ecommerce.service.OrderService;
import rafetefe.ecommerce.service.UserSessionService;

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
        orderRepository.deleteAll();
        productRepository.deleteAll();
        cartRepository.deleteAll();
    }

    private void createOrder(){
        int ownerId = userSessionService.userId;
        int sampleProductId = 1;
        Product sample = new Product(sampleProductId, "productName"+sampleProductId, (double) sampleProductId);
        productRepository.save(sample);
        cartService.addToCart(sampleProductId);
        cartService.submitCart();
    }

    @Test
    void getOngoing(){
        assertTrue(orderService.getOngoingOrders().size() == 0);
        createOrder();
        assertTrue(orderService.getOngoingOrders().size() == 1);
    }

    @Test
    void getCancelled(){
        int userId = userSessionService.userId;
        //verifies both the cancellation process and the obtaining of cancelled orders
        assertTrue(orderService.getCancelledOrders().size() == 0);
        createOrder();
        Order freshOrder = orderRepository.findAllByOwnerId(userId).get(0);
        orderService.cancelOrder(freshOrder.getOrderId());
        assertTrue(orderService.getCancelledOrders().size() == 1);
    }

    @Test
    void getComplete(){
        int userId = userSessionService.userId;
        //verifies both the completion process and the obtaining of completed orders
        assertTrue(orderService.getCompleteOrders().isEmpty());
        createOrder();
        Order freshOrder = orderRepository.findAllByOwnerId(userId).get(0);
        orderService.completeOrder(freshOrder.getOrderId());
        assertTrue(orderService.getCompleteOrders().size() == 1);
    }

    //redoing of upper tests through api calls
    /*
    @GetMapping("/ongoing")
    List<Order> getOngoingOrders();

    @GetMapping("/complete")
    List<Order> getCompleteOrders();

    @GetMapping("/cancelled")
    List<Order> getCancelledOrders();

    @PostMapping("/cancel/{orderId}")
    void cancelOrder(@PathVariable int orderId);

    @PostMapping("/complete/{orderId}")
    void completeOrder(@PathVariable int orderId);
    */
    @Test
    void apiGetOngoing(){
        List<Order> ongoingOrders = getListViaApiAndVerify("/order/ongoing");
        assertTrue(ongoingOrders.isEmpty());//verify returned list is empty

        createOrder();

        ongoingOrders = getListViaApiAndVerify("/order/ongoing");
        assertTrue(ongoingOrders.size() == 1);
    }

    @Test
    void apiGetCancelled(){
        int userId = userSessionService.userId;

        List<Order> cancelledOrders = getListViaApiAndVerify("/order/cancelled");
        assertTrue(cancelledOrders.isEmpty());

        createOrder();

        Order freshOrder = orderRepository.findAllByOwnerId(userId).get(0);

        //cancel order
        assertTrue(webClient.post().uri("/order/cancel/"+freshOrder.getOrderId())
                .exchange()
                .returnResult(WebTestClient.ResponseSpec.class)
                .getStatus().is2xxSuccessful());

        //verify increment of cancelled order list.
        cancelledOrders = getListViaApiAndVerify("/order/cancelled");
        assertTrue(cancelledOrders.size() == 1);
    }

    @Test
    void apiGetCompleted(){
        int userId = userSessionService.userId;

        List<Order> completedOrders = getListViaApiAndVerify("/order/complete");
        assertTrue(completedOrders.isEmpty());

        createOrder();

        Order freshOrder = orderRepository.findAllByOwnerId(userId).get(0);

        assertTrue(webClient.post().uri("/order/complete/"+freshOrder.getOrderId())
                .exchange().returnResult(WebTestClient.ResponseSpec.class)
                .getStatus().is2xxSuccessful());

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
