package com.sofrecom.tickit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access-checks") // More descriptive than "test"
public class TestController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('PRODUCTEUR') or hasRole('PILOTE') or hasRole('DIRECTION') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/producteur")  // Consistent naming
	@PreAuthorize("hasRole('PRODUCTEUR')")
	public String producteurAccess() {
		return "Producteur Board.";
	}
	
	@GetMapping("/pilote")
	@PreAuthorize("hasRole('PILOTE')")
	public String piloteAccess() {
		return "Pilote Board.";
	}
	
	@GetMapping("/direction")  // Consistent naming
	@PreAuthorize("hasRole('DIRECTION')")
	public String directionAccess() {
		return "Direction Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
