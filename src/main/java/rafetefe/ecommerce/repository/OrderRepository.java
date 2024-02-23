package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.domain.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, String> {
    Mono<Order> findByOrderId(int orderId);
    Flux<Order> findAllByOwnerId(int ownerId);
    Flux<Order> findAllByOwnerIdAndStatus(int ownerId, Status status);
}
