package com.ecommerce.orders.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // returns 400 automatically
public class OrderCancellationException extends RuntimeException {

	public OrderCancellationException(String message) {
		super(message);
	}
}