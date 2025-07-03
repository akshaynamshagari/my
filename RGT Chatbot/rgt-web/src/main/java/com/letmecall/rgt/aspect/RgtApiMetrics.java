package com.letmecall.rgt.aspect;

import io.micrometer.core.instrument.Metrics;

public class RgtApiMetrics {

	private static final String INPUT_COUNTER = "entity_input_counter";
	private static final String ERROR_COUNTER = "entity_error_counter";
	private static final String API_TIMER = "entity_api_request_timer";
	private static final String methodTag = "method";

	public static void recordExcutionTime(long entime, String methodName) {
		Metrics.summary(API_TIMER, methodTag, methodName).record(entime);
	}

	public static void metricsForInputCounterCalls(String methodName) {
		Metrics.counter(INPUT_COUNTER, methodTag, methodName).increment();
	}

	public static void metricsForErrorCounterCalls(String statusCode, String methodName) {
		Metrics.counter(ERROR_COUNTER, "status_code", statusCode + "", methodTag, methodName).increment();
	}
}
