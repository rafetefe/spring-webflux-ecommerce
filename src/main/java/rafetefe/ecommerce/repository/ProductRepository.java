package rafetefe.ecommerce.repository;

import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Product;

import java.util.ArrayList;
import java.util.List;
@Repository
public class ProductRepository {

    //Mock DB
    private List<Product> productList;

    ProductRepository(){
        this.productList = new ArrayList<Product>();
    }

    public Product getById(int id){
        return this.productList.get(id);
    }

    public void insertProduct(Product product){
        this.productList.add(product);
    }

    public List<Product> getAll(){
        return this.productList;
    }
}
