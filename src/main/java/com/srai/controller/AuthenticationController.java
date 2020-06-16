package com.srai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.srai.configuration.AuthResponse;
import com.srai.model.MasterTenant;
import com.srai.model.UserLoginDTO;
import com.srai.service.JdbcTemplateService;
import com.srai.service.MasterTenantService;
import com.srai.tenant.hibernate.JwtTokenUtil;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	MasterTenantService masterTenantService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JdbcTemplateService jdbc;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> userLogin(@RequestBody @NotNull UserLoginDTO userLoginDTO) {
		LOGGER.info("userLogin() method call...");
		if (null == userLoginDTO.getUserName() || userLoginDTO.getUserName().isEmpty()) {
			return new ResponseEntity<>("User name is required", HttpStatus.BAD_REQUEST);
		}
		MasterTenant masterTenant = masterTenantService.findByTenantId(userLoginDTO.getTenant());
		if (null == masterTenant) {
			throw new RuntimeException("Please contact service provider.");
		}
		LOGGER.info("database name and user name of the user  :: {} ", masterTenant.getDatabaseName());
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(), userLoginDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		final String token = jwtTokenUtil.generateToken(userDetails.getUsername(), userLoginDTO.getTenant());
		return ResponseEntity.ok(new AuthResponse(userDetails.getUsername(), token));
	}
}
