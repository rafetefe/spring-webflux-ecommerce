package rafetefe.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rafetefe.ecommerce.controller.CartController;
import rafetefe.ecommerce.domain.Cart;
import rafetefe.ecommerce.domain.Order;
import rafetefe.ecommerce.domain.Product;
import rafetefe.ecommerce.repository.CartRepository;
import rafetefe.ecommerce.repository.OrderRepository;
import rafetefe.ecommerce.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class CartService implements CartController {

    /*
        Repetitive access to userService.userId is occurring.
        What should be the scope or cache setting for this design could be implemented?

        TODO: -Design Thought-
        User session could be implemented in a way that stores
        UserId and CurrentCart . And could be kept in a cache
        for to be used during active user sessions. (Cache should be limited)
        (would lesser the time the database is being busy)
        (by keeping the attirbutes of current online users in a ram
         /dependent on the amount of free ram we have in system/)
     */

    private static final Logger LOG = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final UserSessionService userService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                UserSessionService userService,
                OrderRepository orderRepository,
                ProductRepository productRepository){
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    private void initUserCart(){
        int ownerId = userService.userId;
        Cart cart = new Cart(ownerId);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(int productId) {
        int ownerId = userService.userId;
        cartRepository.findByOwnerId(ownerId).ifPresent(
                cart -> {
                    cart.removeByProductId(productId);
                    cartRepository.save(cart);
                }
        );
    }

    @Override
    public void clearCart() {
        int ownerId = userService.userId;
        cartRepository.findByOwnerId(ownerId).ifPresentOrElse(
                (cartExists) -> {
                    cartExists.clearContentList();
                    cartRepository.save(cartExists);
                },
                () -> {//case where cart doesn't exist (else state)
                    this.initUserCart();
                }
        );
    }

    //Should be an atomic event.
    @Override
    public void submitCart() {
        int ownerId = userService.userId;
        cartRepository.findByOwnerId(ownerId).ifPresent(
                cartExists -> {
                    if( !(cartExists.getContent().isEmpty()) ){
                        //if not empty
                        Order newOrder = new Order(cartExists.getContent(), cartExists.getOwnerId());
                        orderRepository.save(newOrder);
                        clearCart();
                    }
                }
        );
        //submits only if cart exists and cart not empty
    }

    @Override
    public void addToCart(int productId) {
        int ownerId = userService.userId;
        Product foundProduct;
        Cart foundCart;

        if(productRepository.findByProductId(productId).isPresent()){
            foundProduct = productRepository.findByProductId(productId).get();
        }else{
            return;
            //don't continue if given product doesn't exists
        }

        if(cartRepository.findByOwnerId(ownerId).isPresent()){
            foundCart = cartRepository.findByOwnerId(ownerId).get();
        }else{//no Cart found, try to create.
            this.initUserCart();
            if(cartRepository.findByOwnerId(ownerId).isPresent()){
                foundCart = cartRepository.findByOwnerId(ownerId).get();
            }else{//still no cart found, infrastructure error.
                return;
            }
        }

        foundCart.addProduct(foundProduct);
        cartRepository.save(foundCart);
    }

    @Override
    public List<Product> getCartContent() {
        int ownerId = userService.userId;
        Cart foundCart;

        //same code snippet as above
        if(cartRepository.findByOwnerId(ownerId).isPresent()){
            foundCart = cartRepository.findByOwnerId(ownerId).get();
        }else{//no Cart found, try to create.
            this.initUserCart();
            if(cartRepository.findByOwnerId(ownerId).isPresent()){
                foundCart = cartRepository.findByOwnerId(ownerId).get();
            }else{//still no cart found, infrastructure error.
                return null;
            }
        }

        return foundCart.getContent();
    }
}
