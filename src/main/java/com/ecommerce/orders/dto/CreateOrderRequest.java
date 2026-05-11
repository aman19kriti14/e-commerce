package com.ecommerce.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

	@NotBlank(message = "Customer name must not be blank")
	private String customerName;

	@NotEmpty(message = "Order must have at least one item")
	@Valid
	private List<OrderItemRequest> items;
}