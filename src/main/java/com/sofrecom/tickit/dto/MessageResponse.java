package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO representing a response message sent to the client.
 */
@Data
@AllArgsConstructor
public class MessageResponse {

	/**
	 * The message content to be sent in the response.
	 */
	private String message;
}
