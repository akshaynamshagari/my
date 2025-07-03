package com.letmecall.rgt.rest.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(MyErrorController.class);

	@GetMapping(value = "/error")
	public String handleError(HttpServletRequest request) {
		logger.debug("handleError :URL={},Status={},ErrorException={},ErrorType={},ExceptionMsg={}",
				request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH),
				request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE),
				request.getAttribute(RequestDispatcher.ERROR_EXCEPTION),
				request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE),
				request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value() || statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "forward:/pagenotfound";
			}
		}
		return "forward:/pagenotfound";
	}

	public String getErrorPath() {
		return null;
	}

}
