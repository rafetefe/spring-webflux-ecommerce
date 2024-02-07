package rafetefe.ecommerce.repository;

import org.springframework.stereotype.Repository;

import rafetefe.ecommerce.domain.Cart;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CartRepository {

    //Mock DB,
    private Map<Integer , Cart> carts;

    public CartRepository(){
        carts = new HashMap<Integer, Cart>();
    }

    public Cart getCartById(Integer id){
        return carts.get(id);
    }

    public void updateCartById(Integer id, Cart newCart){
        carts.put(id, newCart);
    }

}
