package com.example.demo.repositories;

import com.example.demo.dto.ProductListDTO;
import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    @Query(

            """
                    SELECT new com.example.demo.dto.ProductListDTO(
                        p.id, p.name,p.description, p.price, ci.quantity, p.image
                    )
                    FROM CartItem ci
                    JOIN ci.cart c
                    JOIN c.user u
                    JOIN ci.product p
                    WHERE u.id = :userId"""
    )
    public List<ProductListDTO> findCartItems(@Param("userId") Long userId);
}
