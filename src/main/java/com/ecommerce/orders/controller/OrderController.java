package com.ecommerce.orders.controller;

import com.ecommerce.orders.dto.CreateOrderRequest;
import com.ecommerce.orders.dto.OrderResponse;
import com.ecommerce.orders.model.OrderStatus;
import com.ecommerce.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {

		OrderResponse response = orderService.createOrder(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {

		OrderResponse response = orderService.getOrderById(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam(required = false) OrderStatus status) {

		List<OrderResponse> responses = orderService.getAllOrders(status);
		return ResponseEntity.ok(responses);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {

		OrderResponse response = orderService.updateOrderStatus(id, status);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {

		orderService.cancelOrder(id);
		return ResponseEntity.noContent().build();
	}
}