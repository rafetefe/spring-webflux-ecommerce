package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, String> {
    Optional<Cart> findByOwnerId(int ownerId);
}
