package com.letmecall.rgt.config.security;

import com.letmecall.rgt.utility.Constant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthenticationRequest {

	@NotNull(message = "{Authentication.username.notEmpty}")
	@Pattern(regexp = Constant.EMAIL_REG_EXP_PATERN, message = "{Authentication.username.value.pattern}")
	private String userName;

	@NotNull(message = "{Authentication.password.notEmpty}")
	@Size(min = Constant.PASSWORD_MIN_VALUE, max = Constant.PASSWORD_MAX_VALUE, message = "{Authentication.password.value.size}")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AuthenticationRequest [username=" + userName + "]";
	}
}
