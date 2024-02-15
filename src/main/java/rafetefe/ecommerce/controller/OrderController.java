package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rafetefe.ecommerce.domain.Order;
import java.util.List;
@RequestMapping("/order")
public interface OrderController {

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
}
