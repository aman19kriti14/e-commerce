package com.ecommerce.orders.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.orders.dto.CreateOrderRequest;
import com.ecommerce.orders.dto.OrderItemRequest;
import com.ecommerce.orders.dto.OrderItemResponse;
import com.ecommerce.orders.dto.OrderResponse;
import com.ecommerce.orders.exception.OrderCancellationException;
import com.ecommerce.orders.exception.OrderNotFoundException;
import com.ecommerce.orders.model.Order;
import com.ecommerce.orders.model.OrderItem;
import com.ecommerce.orders.model.OrderStatus;
import com.ecommerce.orders.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	@Override
	@Transactional
	public OrderResponse createOrder(CreateOrderRequest request) {

		Order order = Order.builder().customerName(request.getCustomerName()).status(OrderStatus.PENDING).build();

		for (OrderItemRequest itemRequest : request.getItems()) {
			OrderItem item = OrderItem.builder().productName(itemRequest.getProductName())
					.quantity(itemRequest.getQuantity()).price(itemRequest.getPrice()).build();
			order.addItem(item);
		}

		Order savedOrder = orderRepository.save(order);
		orderRepository.flush(); 

		
		Order freshOrder = orderRepository.findByIdWithItems(savedOrder.getId()).orElse(savedOrder);

		return mapToResponse(freshOrder);
	}

	@Override
	@Transactional(readOnly = true) 
	public OrderResponse getOrderById(UUID id) {

		Order order = orderRepository.findByIdWithItems(id)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

		return mapToResponse(order);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders(OrderStatus status) {

		List<Order> orders;

		if (status != null) {
			// filter by status if provided → GET /api/orders?status=PENDING
			orders = orderRepository.findByStatus(status);
		} else {
			// return all orders → GET /api/orders
			orders = orderRepository.findAll();
		}

		return orders.stream().map(this::mapToResponse).toList();
	}

	// ─────────────────────────────────────────
	// UPDATE ORDER STATUS
	// ─────────────────────────────────────────
	@Override
	@Transactional
	public OrderResponse updateOrderStatus(UUID id, OrderStatus newStatus) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

		// Guard: cannot update a cancelled order
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new OrderCancellationException("Cannot update status of a cancelled order");
		}

		order.setStatus(newStatus);
		Order updatedOrder = orderRepository.save(order);

		return mapToResponse(updatedOrder);
	}

	// ─────────────────────────────────────────
	// CANCEL ORDER
	// ─────────────────────────────────────────
	@Override
	@Transactional
	public void cancelOrder(UUID id) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

		// Business rule: only PENDING orders can be cancelled
		if (order.getStatus() != OrderStatus.PENDING) {
			throw new OrderCancellationException(
					"Order can only be cancelled when in PENDING status. " + "Current status: " + order.getStatus());
		}

		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);
	}

	// ─────────────────────────────────────────
	// PRIVATE MAPPER METHODS
	// ─────────────────────────────────────────

	// Entity → Response DTO
	private OrderResponse mapToResponse(Order order) {
		return OrderResponse.builder().id(order.getId()).customerName(order.getCustomerName()).status(order.getStatus())
				.items(order.getItems().stream().map(this::mapItemToResponse).toList()).createdAt(order.getCreatedAt())
				.updatedAt(order.getUpdatedAt()).build();
	}

	// OrderItem Entity → OrderItemResponse DTO
	private OrderItemResponse mapItemToResponse(OrderItem item) {
		return OrderItemResponse.builder().id(item.getId()).productName(item.getProductName())
				.quantity(item.getQuantity()).price(item.getPrice()).build();
	}
}