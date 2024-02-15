package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.domain.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, String> {
    Optional<Order> findByOrderId(int orderId);
    List<Order> findAllByOwnerId(int ownerId);
    List<Order> findAllByOwnerIdAndStatus(int ownerId, Status status);
}
