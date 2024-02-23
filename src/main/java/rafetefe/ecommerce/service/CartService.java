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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.logging.Level.FINE;

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

    private Mono<Cart> initUserCart(){
        int ownerId = userService.userId;
        Cart cart = new Cart(ownerId);
        return cartRepository.save(cart);
    }

    @Override
    public Mono<Cart> removeFromCart(int productId) {
        int ownerId = userService.userId;
        return cartRepository.findByOwnerId(ownerId)
                .switchIfEmpty(Mono.error(new Exception("Cart failed to  found:"+productId)))
                .log(LOG.getName(),FINE)
                .map(foundCart -> {
                    foundCart.removeByProductId(productId);
                    return cartRepository.save(foundCart);
                    }).flatMap(e->e);
    }

    @Override
    public Mono<Cart> clearCart() {
        int ownerId = userService.userId;
        return cartRepository.findByOwnerId(ownerId)
                .switchIfEmpty(this.initUserCart())
                .log(LOG.getName(), FINE)
                .map(foundCart -> {
                    foundCart.clearContentList();
                    return cartRepository.save(foundCart);
                }).flatMap(e->e);


    }

    @Override
    public Mono<Order> submitCart() {
        int ownerId = userService.userId;

        return cartRepository.findByOwnerId(ownerId)
                .onErrorMap(ex-> new Exception("submitCart error:"+ex.getMessage()))
                .filter(cart -> !cart.getContent().isEmpty() )
                .map(foundCart -> {
                    Order newOrder = new Order(foundCart.getContent(), foundCart.getOwnerId());
                    foundCart.clearContentList();
                    return Mono.zip(cartRepository.save(foundCart),orderRepository.save(newOrder))
                            .map(objects -> objects.getT2());
                }).flatMap(e->e);

    }

    @Override
    public Mono<Cart> addToCart(int productId) {
        int ownerId = userService.userId;

        Mono<Product> foundProduct = productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new Exception("productRepo, requested product not found.")))
                .log(LOG.getName(), FINE)
                .onErrorMap(ex->new Exception("productRepo, product query returned error:"+ ex.getMessage()));

        Mono<Cart> foundCart = cartRepository.findByOwnerId(ownerId)
                .switchIfEmpty(this.initUserCart())
                .log(LOG.getName(), FINE)
                .onErrorMap(ex->new Exception("cartRepo, cart query returned error:"+ ex.getMessage()));

        return foundProduct.map(p->
                foundCart.map(
                        cart -> {
                            cart.addProduct(p);
                            return cartRepository.save(cart);
                        }).flatMap(e->e)
        ).flatMap(e->e);


    }

    @Override
    public Flux<Product> getCartContent() {
        int ownerId = userService.userId;
        return cartRepository.findByOwnerId(ownerId)
                .switchIfEmpty(this.initUserCart())
                .log(LOG.getName(), FINE)
                .flatMapMany(cart -> {
                    return Flux.fromIterable(cart.getContent());
                });
    }
}
