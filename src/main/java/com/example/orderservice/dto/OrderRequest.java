package com.example.orderservice.dto;


public record OrderRequest(String productCode,Integer quantity, Double amount) {
}