package com.anipick.backend.version.util;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionValidator {

	private static final Pattern VERSION_PATTERN =
		Pattern.compile("^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$");

	public static boolean checkValidationVersion(String version) {
		return version != null && VERSION_PATTERN.matcher(version).matches();
	}
}
