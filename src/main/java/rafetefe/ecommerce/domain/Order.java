package rafetefe.ecommerce.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
@Document(collection="orders")
public class Order {

    /*DB Attributes*/
    @Id
    private String id;
    @Version
    private Integer version;
    /**/

    @Indexed(unique = true)
    private int orderId;
    //No user service yet. CompoundIndex postponed.
    private int ownerId; //has no setter.
    private List<Product> content;
    private Date dateOrderInitiated;
    private Date dateOrderCompleted;
    private Status status;

    public Order(List<Product> content, int ownerId){
        this.content = content;
        this.ownerId = ownerId;
        this.dateOrderInitiated = new Date();
        this.status = Status.ONGOING;
    }

    public void completeOrder(){
        this.dateOrderCompleted = new Date();
        this.status = Status.COMPLETE;
    }

    public void cancelOrder(){
        this.dateOrderCompleted = new Date();
        this.status = Status.CANCELLED;
    }

    /*Getter and Setters*/

    public Status getStatus(){
        return this.status;
    }

    public List<Product> getContent() {
        return content;
    }

    public void setContent(List<Product> content) {
        this.content = content;
    }

    public Date getDateOrderInitiated() {
        return dateOrderInitiated;
    }

    public void setDateOrderInitiated(Date dateOrderInitiated) {
        this.dateOrderInitiated = dateOrderInitiated;
    }

    public Date getDateOrderCompleted() {
        return dateOrderCompleted;
    }

    public void setDateOrderCompleted(Date dateOrderCompleted) {
        this.dateOrderCompleted = dateOrderCompleted;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


}
