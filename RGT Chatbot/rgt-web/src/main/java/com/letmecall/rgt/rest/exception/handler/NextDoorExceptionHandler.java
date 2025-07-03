package com.letmecall.rgt.rest.exception.handler;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class NextDoorExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(NextDoorExceptionHandler.class);

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<Response<ValidationErrorResponse>> handleConstraintValidationException(
			ConstraintViolationException ex) {
		logger.debug("handleConstraintValidationException :exception={}", ex.getMessage());
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
		}
		Response<ValidationErrorResponse> response = Response.buildResponse(error, StatusType.ERROR,
				HttpStatus.BAD_REQUEST.value(), null);
		logger.debug("handleConstraintValidationException :BAD_REQUEST={}", response);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseEntity<Response<ValidationErrorResponse>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException e) {
		logger.debug("handleMethodArgumentNotValidException :exception={}", e.getMessage());
		ValidationErrorResponse error = new ValidationErrorResponse();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
		}
		Response<ValidationErrorResponse> response = Response.buildResponse(error, StatusType.ERROR,
				HttpStatus.BAD_REQUEST.value(), null);
		logger.debug("handleMethodArgumentNotValidException :BAD_REQUEST={}", response);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ResponseBody
	public ResponseEntity<Response<String>> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex) {
		logger.debug("handleHttpRequestMethodNotSupported :exception={}", ex.getMessage());

		Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
		HttpHeaders headers = new HttpHeaders();
		if (!CollectionUtils.isEmpty(supportedMethods)) {
			headers.setAllow(supportedMethods);
		}
		Response<String> response = Response.buildResponse(ex.getMessage(), StatusType.ERROR,
				HttpStatus.METHOD_NOT_ALLOWED.value(), null);
		logger.debug("handleHttpRequestMethodNotSupported :UNSUPPORTED_MEDIA_TYPE={}", response);
		return new ResponseEntity<>(response, headers, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
		logger.debug("handleNoHandlerFoundException :exception={}", ex.getMessage());
		Response<String> response = Response.buildResponse(
				String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()),
				StatusType.ERROR, HttpStatus.NOT_FOUND.value(), null);
		logger.debug("handleHttpRequestMethodNotSupported :UNSUPPORTED_MEDIA_TYPE={}", response);
		return new ResponseEntity<>(response, ex.getHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ResponseBody
	public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
		logger.debug("handleHttpMediaTypeNotSupported :exception={}", ex.getMessage());
		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		HttpHeaders headers = new HttpHeaders();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			headers.setAccept(mediaTypes);
		}
		Response<String> response = Response.buildResponse(String.format(ex.getMessage()), StatusType.ERROR,
				HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), null);
		logger.debug("handleHttpMediaTypeNotSupported :UNSUPPORTED_MEDIA_TYPE={}", response);
		return new ResponseEntity<>(response, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

}
