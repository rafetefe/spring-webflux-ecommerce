package rafetefe.ecommerce.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Document(collection="carts")
public class Cart {

    /*DB Attributes*/
    @Id
    private String id;
    @Version
    private Integer version;
    /**/

    //ID of the Owner
    @Indexed(unique = true)
    private int ownerId;

    private List<Product> content;

    public Cart(int ownerId){
        this.content = new ArrayList<Product>();
        this.ownerId = ownerId;
    }

    public void clearContentList(){
        this.content = new ArrayList<Product>();
    }

    public List<Product> getContent() {
        return content;
    }

    public void addProduct(Product product) {
        this.content.add(product);
    }

    public void removeByProductId(int removedId){
        //can be made faster by using cartElementNo rather productID
        //but no need to optimize atm
        for (int i = 0; i < content.size(); i++) {
            if(content.get(i).getProductId() == removedId){
                content.remove(i);
                return;
            }
        }
        //content.removeIf(x->x.getId().equals(removedId));

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return ownerId == cart.ownerId && Objects.equals(id, cart.id) && Objects.equals(version, cart.version) && Objects.equals(content, cart.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, ownerId, content);
    }
}
