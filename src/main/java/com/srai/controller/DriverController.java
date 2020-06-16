package com.srai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.srai.service.UserService;

@RestController
public class DriverController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/driver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> addDriver(@RequestBody MultipartFile file, MultipartFile driver) throws Exception {
		return userService.addDriver(file, driver);
	}

	@PostMapping(value = "/datasource")
	public ResponseEntity<Object> dataSourceDetail(@RequestParam Long datasourceId, @RequestParam Integer userId,
			@RequestParam String databasename) throws Exception {
		return userService.dataSourceDetail(datasourceId, userId, databasename);
	}

}
