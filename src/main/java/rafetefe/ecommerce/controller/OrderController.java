package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rafetefe.ecommerce.domain.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
@RequestMapping("/order")
public interface OrderController {

    @GetMapping("/ongoing")
    Flux<Order> getOngoingOrders();

    @GetMapping("/complete")
    Flux<Order> getCompleteOrders();

    @GetMapping("/cancelled")
    Flux<Order> getCancelledOrders();

    @PostMapping("/cancel/{orderId}")
    Mono<Void> cancelOrder(@PathVariable int orderId);

    @PostMapping("/complete/{orderId}")
    Mono<Void> completeOrder(@PathVariable int orderId);
}
