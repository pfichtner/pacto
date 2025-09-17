package com.github.pfichtner.pacto;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
		// TODO switch to random
		LocalDateTime localDateTime = localDateTime();
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public LocalDate localDate() {
		// TODO switch to random
		return localDateTime().toLocalDate();
	}

	public LocalDateTime localDateTime() {
		// TODO switch to random
		return LocalDateTime.of(2025, 9, 7, 14, 30, 0);
	}

}
