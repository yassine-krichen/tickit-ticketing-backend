package com.sofrecom.tickit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
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

	@GetMapping("/prod")
	@PreAuthorize("hasRole('PRODUCTEUR')")
	public String producteurAccess() {
		return "Producteur Board.";
	}
	
	@GetMapping("/pilo")
	@PreAuthorize("hasRole('PILOTE')")
	public String piloteAccess() {
		return "Pilote Board.";
	}
	
	@GetMapping("/dir")
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
