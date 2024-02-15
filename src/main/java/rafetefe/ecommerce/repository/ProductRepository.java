package rafetefe.ecommerce.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, String>, CrudRepository<Product, String> {
    Optional<Product> findByProductId(int productId);

    void deleteByProductId(int productId);
}
