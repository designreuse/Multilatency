package com.srai.util;

public abstract class ValidationUtil {
	public static boolean isNumber(String data) {
		return data.matches("[0-9]+");
	}

	public static boolean isCharacter(String data) {
		return data.matches("[a-zA-Z]+");
	}
	
	public static boolean isAddress(String data) {
		return data.matches("[a-z A-Z 0-9]+");
	}
}
