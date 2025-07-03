package com.letmecall.rgt.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TokenData {

	private final ConcurrentMap<String, String> map;

	public TokenData(String userName, String customerId) {
		map = new ConcurrentHashMap<>();
		map.put("userName", userName);
		map.put("customerId", customerId);
	}

	public String getAttribute(String key) {
		return map.get(key);
	}

	@Override
	public String toString() {
		return map.get("userName");
	}

}
