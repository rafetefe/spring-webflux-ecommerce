package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Product;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, String> {
    Mono<Product> findByProductId(int productId);

    Mono<Void> deleteByProductId(int productId);
}
