package com.letmecall.rgt.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class CustomRespose<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	protected StatusType statusType;
	protected String code;
	protected String message;
	@JsonInclude(Include.NON_NULL)
	private Integer successCount;
	@JsonInclude(Include.NON_NULL)
	private Integer errorCount;
	@JsonInclude(Include.NON_NULL)
	private String errorType;
	@JsonInclude(Include.NON_NULL)
	private T successResponse;
	@JsonInclude(Include.NON_NULL)
	private String error;
	@JsonInclude(Include.NON_NULL)
	private T errorRepose;

	public CustomRespose() {
		super();
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public void setStatusType(StatusType statusType) {
		this.statusType = statusType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public T getSuccessResponse() {
		return successResponse;
	}

	public void setSuccessResponse(T successResponse) {
		this.successResponse = successResponse;
	}

	public T getErrorRepose() {
		return errorRepose;
	}

	public void setErrorRepose(T errorRepose) {
		this.errorRepose = errorRepose;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public static <T> CustomRespose<T> buildResponse(T successResponse, T errorRepose, Integer successCount,
			Integer errorCount, StatusType statusType, Integer statuscode, String message) {
		CustomRespose<T> response = new CustomRespose<>();
		response.setStatusType(statusType);
		response.setSuccessResponse(successResponse);
		response.setErrorRepose(errorRepose);
		response.setMessage(message);
		response.setSuccessCount(successCount);
		response.setErrorCount(errorCount);
		response.setCode(statuscode.toString());
		return response;
	}
}
