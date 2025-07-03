package com.letmecall.rgt.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.model.Response;
import com.letmecall.rgt.model.StatusType;

@Aspect
@Component
public class ApiMetricsAspect {

	@Pointcut("@annotation(com.letmecall.rgt.aspect.ApiMetrics)")
	public void metricPointCut() {

	}

	@Around("metricPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Long beginTime = System.currentTimeMillis();
		Logger log = LoggerFactory.getLogger(point.getTarget().getClass());
		String methodName = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(ApiMetrics.class)
				.methodName();
		recordApiCall(methodName);
		Object result = point.proceed();
		recordErrorsIfFound(result, methodName);
		Long endTime = System.currentTimeMillis() - beginTime;
		recordExecutiontime(endTime, methodName, log);
		return result;
	}

	private void recordExecutiontime(Long endTime, String methodName, Logger log) {
		log.debug("MethodName={},duration={}", methodName, endTime);
		RgtApiMetrics.recordExcutionTime(endTime, methodName);

	}

	@SuppressWarnings("rawtypes")
	private void recordErrorsIfFound(Object result, String methodName) {
		ResponseEntity responseEntity = (ResponseEntity) result;
		Response rgtRes =(Response)responseEntity.getBody();
		if (!rgtRes.getStatusType().equals(StatusType.SUCCESS)) {
			RgtApiMetrics.metricsForErrorCounterCalls(rgtRes.getCode(), methodName);
		}
	}

	private void recordApiCall(String methodName) {
		RgtApiMetrics.metricsForInputCounterCalls(methodName);
	}
}