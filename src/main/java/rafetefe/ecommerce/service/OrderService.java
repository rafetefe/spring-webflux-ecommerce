package rafetefe.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.OrderController;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Status;
import rafetefe.ecommerce.repository.OrderRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
public class OrderService implements OrderController {

    private final OrderRepository orderRepository;
    private final UserSessionService userSessionService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserSessionService userSessionService){
        this.orderRepository = orderRepository;
        this.userSessionService = userSessionService;
    }

    @Override
    public List<Order> getOngoingOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.ONGOING);
    }

    @Override
    public List<Order> getCompleteOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.COMPLETE);
    }

    @Override
    public List<Order> getCancelledOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.CANCELLED);
    }

    @Override
    public void cancelOrder(int orderId) {
        int ownerId = userSessionService.userId;
        orderRepository.findByOrderId(orderId).ifPresent(
                foundOrder -> {
                    foundOrder.cancelOrder();
                    orderRepository.save(foundOrder);
                }
        );
    }

    @Override
    public void completeOrder(int orderId) {
        int ownerId = userSessionService.userId;
        orderRepository.findByOrderId(orderId).ifPresent(
                foundOrder -> {
                    foundOrder.completeOrder();
                    orderRepository.save(foundOrder);
                }
        );
    }


    //Aborted| So to leave the filtering of status to database / spring data.
    //performance measure can clarify: which one is better.

//    private List<Order> filterParalelStream(Iterable<Order> iter, Status status){
//        return StreamSupport.stream(iter.spliterator(), true)
//                .filter(i->i.getStatus().equals(status))
//                .toList();
//    }
//
//    @Override
//    public List<Order> getOngoingOrders() {
//        return filterParalelStream(orderRepository.findAll(), Status.ONGOING);
//    }
//
//    @Override
//    public List<Order> getCompleteOrders() {
//        return filterParalelStream(orderRepository.findAll(), Status.COMPLETE);
//    }
//
//    @Override
//    public List<Order> getCancelledOrders() {
//        return filterParalelStream(orderRepository.findAll(), Status.CANCELED);
//    }


}
