package com.letmecall.rgt.rest.exception.handler;

import java.util.ArrayList;
import java.util.List;

import com.letmecall.rgt.domain.common.BaseResponse;

public class ValidationErrorResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Violation> violations = new ArrayList<>();

	public List<Violation> getViolations() {
		return violations;
	}

	public void setViolations(List<Violation> violations) {
		this.violations = violations;
	}
}
