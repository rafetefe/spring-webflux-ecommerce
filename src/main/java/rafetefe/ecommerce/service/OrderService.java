package rafetefe.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.OrderController;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Status;
import rafetefe.ecommerce.repository.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
public class OrderService implements OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
    private final OrderRepository orderRepository;
    private final UserSessionService userSessionService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserSessionService userSessionService){
        this.orderRepository = orderRepository;
        this.userSessionService = userSessionService;
    }

    @Override
    public Flux<Order> getOngoingOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.ONGOING)
                .log(LOG.getName(), FINE)
                .onErrorMap(ex-> new Exception("getOngoingOrders error:"+ex.getMessage()));
    }

    @Override
    public Flux<Order> getCompleteOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.COMPLETE)
                .log(LOG.getName(), FINE)
                .onErrorMap(ex-> new Exception("getCompleteOrders error:"+ex.getMessage()));
    }

    @Override
    public Flux<Order> getCancelledOrders() {
        int ownerId = userSessionService.userId;
        return this.orderRepository.findAllByOwnerIdAndStatus(ownerId, Status.CANCELLED)
                .log(LOG.getName(), FINE)
                .onErrorMap(ex-> new Exception("getCancelledOrders error:"+ex.getMessage()));
    }


    @Override
    public Mono<Void> cancelOrder(int orderId) {
        int ownerId = userSessionService.userId;

        return orderRepository.findByOrderId(orderId)
                .log(LOG.getName(), FINE)
                .onErrorMap(ex -> new Exception("cancelOrder error:"+ ex.getMessage()))
                .map(foundOrder ->  {
                    foundOrder.cancelOrder();
                    return orderRepository.save(foundOrder);
                }).flatMap(e->e).then();

    }

    @Override
    public Mono<Void> completeOrder(int orderId) {
        int ownerId = userSessionService.userId;

        return orderRepository.findByOrderId(orderId)
                .switchIfEmpty(Mono.error(new Exception
                        ("completeOrder error: findByOrderId returned nothing for given orderId, "+orderId)
                ))
                .log(LOG.getName(), FINE)
                .onErrorMap(ex -> new Exception("completeOrder error:"+ex.getMessage()))
                .map(foundOrder -> {
                    foundOrder.completeOrder();
                    return orderRepository.save(foundOrder);
                }).flatMap(e->e).then();

    }



}
