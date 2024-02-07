package rafetefe.ecommerce.domain;

import java.util.Date;
import java.util.List;

public class Order {
    private List<Product> content;
    private Date orderInitiated;
    private Date orderCompleted;
    private Status status;

    public Order(List<Product> content){
        this.content = content;
        this.orderInitiated = new Date();
        this.status = Status.ONGOING;
    }

//    public List<Product> getContent() {
//        return content;
//    }
//
//    public void completeOrder(){
//        this.orderCompleted = new Date();
//        this.status = Status.COMPLETE;
//    }
//
//    public void cancelOrder(){
//        this.orderCompleted = new Date();
//        this.status = Status.CANCELED;
//    }

    public Status getStatus(){
        return this.status;
    }

}
