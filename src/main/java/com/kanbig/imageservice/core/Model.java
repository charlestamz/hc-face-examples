package com.kanbig.imageservice.core;

import java.util.HashMap;
import java.util.Map;

public class Model extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8545912414138870941L;

	public static Model build() {
		return new Model();
	}

	public Model add(String key, Object val) {
		put(key, val);
		return this;
	}

	public Model addAll(Map<String, Object> data) {
		putAll(data);
		return this;
	}
}
