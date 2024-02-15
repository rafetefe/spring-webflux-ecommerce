package rafetefe.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.ProductController;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.ProductRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
public class ProductService implements ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product body) {
        try{
            productRepository.save(body);
            LOG.info("Product" + body + "saved successfully");
            return body;
        }catch(Exception e){
            throw new RuntimeException("Error while trying to create:" + e + "\n Provided Body:" + body);
        }
    }

    @Override
    public Product getProduct(int productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(  () -> new RuntimeException("Product:"+productId+" not found."));
        LOG.info("LOG:req findByProductId Successful:"+productId);
        return product;
    }

    @Override
    public List<Product> getAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), true).toList();
    }

    @Override
    public ResponseEntity deleteProduct(int productId) {
        //idempotent function
        //
        LOG.info("Deletion request received for:"+productId);
        boolean success = false;

        success = productRepository.findByProductId(productId).isPresent();

        if(success){
            productRepository.deleteByProductId(productId);
            LOG.info("Deletion for"+productId+"completed.");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }else{
            LOG.info("Deletion for"+productId+":no entry found");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public String webClientTest(){
        return "hello";
    }

}
