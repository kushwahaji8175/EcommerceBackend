package com.example.demo.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private Long productId;
    private Integer quantity;
    private String address;
    private String contactNumber;
}
