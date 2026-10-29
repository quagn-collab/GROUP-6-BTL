package com.auction.demo.ui.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

public final class MoneyFormat {

	private static final DecimalFormat FORMAT;

	static {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setGroupingSeparator('.');
		FORMAT = new DecimalFormat("#,##0", symbols);
	}

	private MoneyFormat() {
	}

	public static String vnd(BigDecimal amount) {
		if (amount == null) {
			return "0 VNĐ";
		}
		return FORMAT.format(amount) + " VNĐ";
	}

	public static String countdown(LocalDateTime end) {
		if (end == null) {
			return "--";
		}
		Duration left = Duration.between(LocalDateTime.now(), end);
		if (left.isNegative()) {
			return "Đã kết thúc";
		}
		long hours = left.toHours();
		long minutes = left.toMinutesPart();
		long seconds = left.toSecondsPart();
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
