package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.*;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Transactional
    public OrderDTO createOrder(Long userId, OrderRequest or){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));


        Order order = new Order();
        order.setUser(user);
        order.setAddress(or.getAddress());
        order.setPhoneNumber(or.getContactNumber());
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreatedAt(LocalDateTime.now());

        Product product = productRepository.findById(or.getProductId())
                .orElseThrow(()-> new EntityNotFoundException("Product not found with id: "+or.getProductId()));

        if(product.getQuantity() == null){
            throw new IllegalStateException("Product quantity is not set for product "+product.getName());
        }
        if(product.getQuantity() < or.getQuantity()){
            throw new InsufficientStockException("Not enough stock for product "+product.getName());
        }
        product.setQuantity(product.getQuantity() - or.getQuantity());
        productRepository.save(product);
        var orderItem=new OrderItem(null,order,product,or.getQuantity(),product.getPrice());
        order.getItems().add(orderItem);
        Order savedOrder = orderRepository.save(order);

        try{
            emailService.sendOrderConfirmation(savedOrder);
        }catch (MailException e){
            logger.error("Failed to send order confirmation email for order ID "+savedOrder.getId(), e);
        }
        var od= orderMapper.toDTO(savedOrder);
        var oid=orderMapper.toOrderItemDTO(orderItem);
        od.setOrderItems(List.of(oid));
        return od;
    }

    public List<ProductOrderDto> getAllOrders(){
        return orderRepository.findAllOrdersMade();
    }

    public List<ProductOrderDto> getUserOrders(Long userId){
        return orderRepository.findProductsOrderedByUser(userId);
    }

    public OrderDTO updateOrderStatus(Long orderId,Order.OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }
}
