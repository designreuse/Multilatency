package com.srai.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.srai.configuration.JWTConstants;
import com.srai.model.MasterTenant;
import com.srai.service.MasterTenantService;
import com.srai.tenant.TenantContext;
import com.srai.tenant.hibernate.JwtTokenUtil;
import com.srai.tenant.hibernate.MultiTenantConnectionProviderImpl;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private MasterTenantService masterTenantService;

	@Autowired
	private MultiTenantConnectionProviderImpl multi;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		String header = httpServletRequest.getHeader(JWTConstants.HEADER_STRING);
		String username = null;
		String audience = null; // tenantOrClientId
		String authToken = null;
		if (header != null && header.startsWith(JWTConstants.TOKEN_PREFIX)) {
			authToken = header.replace(JWTConstants.TOKEN_PREFIX, "");
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				audience = jwtTokenUtil.getAudienceFromToken(authToken);
				logger.info("An e {}", TenantContext.getCurrentTenant());
			} catch (IllegalArgumentException ex) {
				logger.error("An error during getting username from token", ex);
				throw new IllegalArgumentException("Authentication Failed. Username or Password not valid.", ex);
			} catch (ExpiredJwtException ex) {
				logger.warn("The token is expired and not valid anymore", ex);
			} catch (SignatureException ex) {
				logger.error("Authentication Failed. Username or Password not valid.", ex);
				throw new SignatureException("Authentication Failed. Username or Password not valid.", ex);
			}
		} else {
			
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				MasterTenant masterTenant = masterTenantService.findByTenantId(audience);
				logger.info("master tenant detail {}", masterTenant);
				if (null == masterTenant) {
					logger.error("An error during getting tenant name");
					throw new BadCredentialsException("Invalid tenant and user.");
				}
				TenantContext.clear();
				TenantContext.setCurrentTenant(masterTenant.getDatabaseName());
				try {
					multi.getConnection(TenantContext.getCurrentTenant());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				logger.info("authenticated user " + username + ", setting security context");
				logger.error("current tenant ::: {} ", TenantContext.getCurrentTenant());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
