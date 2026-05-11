package com.ecommerce.orders.scheduler;

import com.ecommerce.orders.model.Order;
import com.ecommerce.orders.model.OrderStatus;
import com.ecommerce.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

	private final OrderRepository orderRepository;

	@Scheduled(fixedRate = 300000)
	@Transactional
	public void promotePendingOrders() {

		log.info("Scheduler running — checking for PENDING orders...");

		List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

		if (pendingOrders.isEmpty()) {
			log.info("No PENDING orders found. Nothing to update.");
			return;
		}

		log.info("Found {} PENDING order(s). Updating to PROCESSING...", pendingOrders.size());

		pendingOrders.forEach(order -> order.setStatus(OrderStatus.PROCESSING));

		orderRepository.saveAll(pendingOrders);

		log.info("Successfully updated {} order(s) to PROCESSING.", pendingOrders.size());
	}
}