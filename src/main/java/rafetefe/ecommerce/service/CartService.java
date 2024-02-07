package rafetefe.ecommerce.service;

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

@RestController
public class CartService implements CartController {

    /*
        Repetitive access to userService.userId is occurring.
        What should be the scope or cache setting for this design could be implemented?
     */

    private CartRepository cartRepository;
    private UserSessionService userService;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                UserSessionService userService,
                OrderRepository orderRepository,
                ProductRepository productRepository){
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void removeFromCart(int productID) {
        Integer userID = userService.userId;
        Cart userCart = cartRepository.getCartById(userID);

        userCart.removeProductById(productID);
        cartRepository.updateCartById(userID, userCart);
    }

    @Override
    public void clearCart() {
        Integer userId = userService.userId;
        Cart emptyCart = new Cart();
        cartRepository.updateCartById(userId, emptyCart);
    }

    //Should be an atomic event.
    @Override
    public void submitCart() {
        Integer userId = userService.userId;
        Cart userCart = cartRepository.getCartById(userId);
        Order newOrder = new Order(userCart.getContent());

        //push cart content into order
        orderRepository.insertOrder(newOrder);
        //clear cart
        cartRepository.updateCartById(userId, new Cart());
    }

    @Override
    public void addToCart(int productId) {
        Integer userID = userService.userId;
        Cart userCart = cartRepository.getCartById(userID);
        Product selectedProduct = productRepository.getById(productId);

        userCart.addProduct(selectedProduct);
        cartRepository.updateCartById(userID, userCart);
    }
}
