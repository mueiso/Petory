package com.study.petory.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String home() { return "forward:/index.html";}

	@GetMapping("/login")
	public String login() { return "forward:/login.html";}

	@GetMapping("/animalPlace")
	public String animalPlace() { return "forward:/map.html";}

	// @GetMapping("community")
	// public String community() {}
	//
	// @GetMapping("market")
	// public String market() {}

}
