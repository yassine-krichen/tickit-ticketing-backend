package com.sofrecom.tickit.model;




public class Api_Response {
  
	private String code_response;
	private String message_response;
	public Api_Response(String code_response, String message_response) {
		super();
		this.code_response = code_response;
		this.message_response = message_response;
	}
	public Api_Response() {
		super();
	}
	public String getCode_response() {
		return code_response;
	}
	public void setCode_response(String code_response) {
		this.code_response = code_response;
	}
	public String getMessage_response() {
		return message_response;
	}
	public void setMessage_response(String message_response) {
		this.message_response = message_response;
	}
	
	
}
