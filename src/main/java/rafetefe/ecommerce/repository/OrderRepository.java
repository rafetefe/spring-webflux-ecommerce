package rafetefe.ecommerce.repository;

import org.springframework.stereotype.Repository;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Status;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    //Mock DB
    private List<Order> orders;

    OrderRepository(){
        orders = new ArrayList<Order>();
    }

    /*
        Returns all orders.
     */
    public List<Order> getOrders(){
        return orders;
    }

    /*
        Since this is sort of an db indexing job, I'm writing the order filter func in the repo.
        Returns a order-list, filtered by their Status type.
     */
    public List<Order> getOrders(Status status){
        switch (status){
            case ONGOING:   return orders.stream().filter(order -> order.getStatus() == Status.ONGOING).toList();
            case COMPLETE:  return orders.stream().filter(order -> order.getStatus() == Status.COMPLETE).toList();
            case CANCELED:  return orders.stream().filter(order -> order.getStatus() == Status.CANCELED).toList();
        }
        return null;
    }

    public void insertOrder(Order order){
        orders.add(order);
    }

    public void dropOrder(int Id){

    }

    public void dropOrder(Order order){

    }
}
