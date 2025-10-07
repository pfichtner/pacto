package com.github.pfichtner.pacto;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class DeterministicDataFactory {

	private final Random random;

	public DeterministicDataFactory(String seedFromStringHash) {
		this(deterministicHashCode(seedFromStringHash));
	}

	public DeterministicDataFactory(int seed) {
		random = new Random(seed);
	}

	private static int deterministicHashCode(String string) {
		return range(0, string.length()).map(string::charAt).reduce(0, (h, c) -> 31 * h + c);
	}

	public float floatValue() {
		return random.nextFloat();
	}

	public double doubleValue() {
		return random.nextDouble();
	}

	public int intValue() {
		return random.nextInt();
	}

	public long longValue() {
		return random.nextLong();
	}

	public boolean booleanValue() {
		return random.nextBoolean();
	}

	public Number number() {
		return BigDecimal.valueOf(doubleValue());
	}

	public String string() {
		return string(intValue());
	}

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	public String string(int length) {
		return random.ints(length, 0, CHARACTERS.length()).mapToObj(CHARACTERS::charAt).map(Object::toString)
				.collect(joining());
	}

	public String hexString() {
		return hexString(random.nextInt());
	}

	public String hexString(int digits) {
		long value = random.nextLong();
		String hex = Long.toHexString(value);
		if (hex.length() > digits) {
			return hex.substring(0, digits);
		} else if (hex.length() < digits) {
			return "0".repeat(digits - hex.length()) + hex;
		} else {
			return hex;
		}
	}

	public UUID uuid() {
		byte[] randomBytes = new byte[16];
		random.nextBytes(randomBytes);
		return UUID.nameUUIDFromBytes(randomBytes);
	}

	public String ipv4Address() {
		return format("%d.%d.%d.%d", random.nextInt(256), random.nextInt(256), random.nextInt(256),
				random.nextInt(256));
	}

	public Date date() {
		LocalDateTime localDateTime = localDateTime();
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public LocalDate localDate() {
		return localDateTime().toLocalDate();
	}

	public LocalDateTime localDateTime() {
		int year = year();
		int month = month();
		return LocalDateTime.of(year, month, dayOfMonth(year, month), hour(), minutes(), seconds());
	}

	public int year() {
		return fromTo(1900, 9999);
	}

	public int month() {
		return fromTo(1, 12);
	}

	public int dayOfMonth(int year, int month) {
		return fromTo(1, YearMonth.of(year, month).lengthOfMonth());
	}

	public int dayOfMonth() {
		return fromTo(1, 31);
	}

	public int hour() {
		return fromTo(0, 23);
	}

	public int minutes() {
		return fromTo(0, 59);
	}

	public int seconds() {
		return fromTo(0, 59);
	}

	public <T extends Enum<?>> T enumValue(Class<T> enumClass) {
		T[] enumConstants = enumClass.getEnumConstants();
		return enumConstants[fromTo(0, enumConstants.length)];
	}

	private int fromTo(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

}
