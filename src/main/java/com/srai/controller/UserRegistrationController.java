package com.srai.controller;

import java.net.MalformedURLException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.srai.model.User;
import com.srai.service.UserService;

@RestController
public class UserRegistrationController {

	@Autowired
	private UserService userservice;

	@PostMapping("/user")
	public User registeruser(@RequestBody User user) throws MalformedURLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		return userservice.save(user);
	}
}
