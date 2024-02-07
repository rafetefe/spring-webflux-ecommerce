package rafetefe.ecommerce.domain;

public class Product {
    private int id;
    private String name;
    private Double price;

    public Product(int id, String name, Double price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Product(){
        this.id = 0;
        this.name="namelessProduct";
        this.price= 0.0;
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

    public int getId(){return this.id;}
}
