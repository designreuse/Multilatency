package com.srai.model;

import java.io.Serializable;

/**
 * @author Md. Amran Hossain
 */
public class UserLoginDTO implements Serializable {

	private String userName;
	private String password;
	private String tenant;

	public UserLoginDTO() {
	}

	public UserLoginDTO(String userName, String password, String tenant) {
		this.userName = userName;
		this.password = password;
		this.tenant = tenant;
	}

	public String getUserName() {
		return userName;
	}

	public UserLoginDTO setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserLoginDTO setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

}
