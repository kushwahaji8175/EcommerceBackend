package com.example.demo.dto;



import java.math.BigDecimal;



public record ProductOrderDto( Long productId,
                               String name,
                               String description,
                               BigDecimal price,
                               String image,
                               Integer quantity) {
}
