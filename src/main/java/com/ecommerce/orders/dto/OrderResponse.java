package com.ecommerce.orders.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.ecommerce.orders.model.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	private UUID id;
	private String customerName;
	private OrderStatus status;
	private List<OrderItemResponse> items;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}