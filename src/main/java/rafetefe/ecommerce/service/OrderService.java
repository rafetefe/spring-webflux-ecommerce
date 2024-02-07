package rafetefe.ecommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.OrderController;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Status;
import rafetefe.ecommerce.repository.OrderRepository;

import java.util.List;

@RestController
public class OrderService implements OrderController {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getOngoingOrders() {
       return orderRepository.getOrders(Status.ONGOING);
    }

    @Override
    public List<Order> getCompleteOrders() {
        return orderRepository.getOrders(Status.COMPLETE);
    }

    @Override
    public List<Order> getCancelledOrders() {
        return orderRepository.getOrders(Status.CANCELED);
    }
}
