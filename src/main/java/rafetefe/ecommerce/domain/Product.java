package rafetefe.ecommerce.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection="products")
public class Product {

    /*DB Attributes*/
    @Id
    private String id;

    @Version
    private Integer version;
    /**/

    @Indexed(unique = true)
    private int productId;

    private String name;
    private Double price;

    public Product(int productId, String name, Double price){
        this.productId = productId;
        this.name = name;
        this.price = price;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setPrice(Double price){
        this.price=price;
    }

    public String getName(){
        return this.name;
    }

    public Double getPrice(){
        return this.price;
    }

    public Integer getId(){
        return this.productId;
    }

    public boolean contentWiseEqual(Object target){
        Product typeCast = (Product) target;

        return  this.getId().equals(    typeCast.getId()) &&
                this.getName().equals(  typeCast.getName()) &&
                this.getPrice().equals( typeCast.getPrice()) &&
                this.getClass().equals( target.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId && Objects.equals(id, product.id) && Objects.equals(version, product.version) && Objects.equals(name, product.name) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, productId, name, price);
    }
}
