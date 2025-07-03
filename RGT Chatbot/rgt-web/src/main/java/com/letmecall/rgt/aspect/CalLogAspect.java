package com.letmecall.rgt.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;


@Aspect
@Component
public class CalLogAspect {

	@Pointcut("@annotation(com.letmecall.rgt.aspect.CalLog)")
	public void logPointCut() {
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Logger log = LoggerFactory.getLogger(point.getTarget().getClass());
		writeRequest(point,log);
		Object result = point.proceed();
		writeResponse(point, result,log);
		return result;
	}

	public void writeRequest(ProceedingJoinPoint point,Logger log) {
		if (point != null) {
			MethodSignature signature = (MethodSignature) point.getSignature();
			Method method = signature.getMethod();
			String methodName = method.getName();
			Object[] ars = point.getArgs();

			List<String> list = new ArrayList<>();

			for (Object obj : ars) {
				list.add(new Gson().toJson(obj));
			}

			if (!list.isEmpty()) {
				log.debug("MethodName={},Request={}", methodName, list.toString());
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void writeResponse(ProceedingJoinPoint point, Object result,Logger log) {
		if (point != null && result != null) {
			MethodSignature signature = (MethodSignature) point.getSignature();
			Method method = signature.getMethod();
			String methodName = method.getName();
			ResponseEntity responseEntity = (ResponseEntity) result;
			String gsonString = new Gson().toJson(responseEntity.getBody());
			if (gsonString != null) {
				log.debug("MethodName={},Response={}", methodName, gsonString);
			}
		}
	}

}
