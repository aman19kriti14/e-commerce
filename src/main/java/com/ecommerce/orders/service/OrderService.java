package com.ecommerce.orders.service;

import com.ecommerce.orders.dto.CreateOrderRequest;
import com.ecommerce.orders.dto.OrderResponse;
import com.ecommerce.orders.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

	OrderResponse createOrder(CreateOrderRequest request);

	OrderResponse getOrderById(UUID id);

	List<OrderResponse> getAllOrders(OrderStatus status);

	OrderResponse updateOrderStatus(UUID id, OrderStatus newStatus);

	void cancelOrder(UUID id);
}