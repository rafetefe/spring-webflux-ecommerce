package rafetefe.ecommerce.domain;

import java.util.List;
import java.util.ArrayList;

public class Cart {

    private List<Product> content;

    public Cart(){
        this.content = new ArrayList<Product>();
    }

    public List<Product> getContent() {
        return content;
    }

    public void addProduct(Product product) {
        this.content.add(product);
    }

    public void removeProductById(int removedId){
        //can be made faster by using cartElementNo rather productID
        //but no need to optimize atm
        for (int i = 0; i < content.size(); i++) {
            if(content.get(i).getId() == removedId){
                content.remove(i);
                break;
            }
        }
    }
}
