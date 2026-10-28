package com.auction.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private DateTimeUtil() {
	}

	public static String format(LocalDateTime time) {
		return time == null ? null : time.format(FMT);
	}

	public static LocalDateTime parse(String value) {
		return LocalDateTime.parse(value, FMT);
	}

	public static boolean isBeforeOrEqual(LocalDateTime a, LocalDateTime b) {
		return !a.isAfter(b);
	}
}
