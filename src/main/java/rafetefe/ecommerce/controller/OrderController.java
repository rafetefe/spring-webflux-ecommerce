package rafetefe.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
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

}
