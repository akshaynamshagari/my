package com.letmecall.rgt.config.security;

import com.letmecall.rgt.utility.Constant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthenticationManualRequest extends AuthenticationRequest {

	@NotNull(message = "{Authentication.password.notEmpty}")
	@Size(min = Constant.PASSWORD_MIN_VALUE, max = Constant.PASSWORD_MAX_VALUE, message = "{Authentication.password.value.size}")
	private String confirmPassword;

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
