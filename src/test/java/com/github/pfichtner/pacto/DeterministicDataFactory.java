package com.github.pfichtner.pacto;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class DeterministicDataFactory {

	private final Random random;

	public DeterministicDataFactory(String string) {
		this(deterministicHashCode(string));
	}

	public DeterministicDataFactory(int seed) {
		random = new Random(seed);
	}

	private static int deterministicHashCode(String string) {
		return range(0, string.length()).map(i -> string.charAt(i)).reduce(0, (h, c) -> 31 * h + c);
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
		long minMillis = -8_640_000_000_000_000L;
		long maxMillis = +8_640_000_000_000_000L;
		long millis = minMillis + abs(random.nextLong()) % (maxMillis - minMillis + 1);
		return new Date(millis);
	}

	public LocalDate localDate() {
		long minDay = LocalDate.of(-999_999_999, 01, 01).toEpochDay();
		long maxDay = LocalDate.of(+999_999_999, 12, 31).toEpochDay();

		long randomDay = minDay + abs(random.nextLong()) % (maxDay - minDay + 1);
		return LocalDate.ofEpochDay(randomDay);
	}

	public LocalDateTime localDateTime() {
		LocalDate date = localDate();
		int hour = random.nextInt(24);
		int minute = random.nextInt(60);
		int second = random.nextInt(60);
		int nano = random.nextInt(1_000_000_000);
		return LocalDateTime.of(date, LocalTime.of(hour, minute, second, nano));
	}

}
