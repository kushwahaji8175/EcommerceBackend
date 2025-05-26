package com.example.demo.repositories;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.dto.ProductOrderDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(""" 
            SELECT new com.example.demo.dto.ProductOrderDto(p.id, p.name,p.description, p.price, p.image, oi.quantity) 
            FROM OrderItem oi 
            JOIN oi.product p 
            JOIN oi.order o 
            JOIN o.user u 
            WHERE u.id = :userId
            """)
    List<ProductOrderDto> findProductsOrderedByUser(@Param("userId") Long userId);
    @Query(""" 
            SELECT new com.example.demo.dto.ProductOrderDto(p.id, p.name,p.description, p.price, p.image, oi.quantity) 
            FROM OrderItem oi 
            JOIN oi.product p 
            JOIN oi.order o 
            JOIN o.user u 
            WHERE u.id = :userId
            """)
    List<ProductOrderDto> findAllOrdersMade();
}
