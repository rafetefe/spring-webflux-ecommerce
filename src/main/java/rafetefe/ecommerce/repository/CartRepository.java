package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Product;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository                                                   //string is for mongodb entity id.
public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
    Mono<Cart> findByOwnerId(int ownerId);                   //different from our business logic id.
}
