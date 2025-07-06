package com.study.petory.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String home() { return "forward:/index.html";}

	@GetMapping("/login")
	public String login() { return "forward:/login.html";}

	@GetMapping("/petPlace")
	public String petPlace() { return "forward:/map.html";}

	@GetMapping("/community")
	public String community() {return "forward:/community.html";}

	@GetMapping("/market")
	public String market() {return "forward:/market.html";}

	@GetMapping("/myCalendar")
	public String myCalendar() {return "forward:/calendar.html";}

}
