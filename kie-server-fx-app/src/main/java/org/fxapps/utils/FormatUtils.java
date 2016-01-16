package org.fxapps.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class FormatUtils {

	private FormatUtils() {
	}

	public static <T extends Object> String toPlainText(List<T> l) {
		return toPlainText(l, Collections.emptyList());
	}

	public static <T extends Object> String toCSV(List<T> l) {
		return toCSV(l, Collections.emptyList(), ",", true, true);
	}

	public static <T extends Object> String toPlainText(List<T> l,
			List<String> ignore) {
		if (l == null || l.size() == 0) {
			return "";
		}
		String lineSeparator = System.getProperty("line.separator");
		String finalText = l.stream().map(e -> {
			List<String> fields = filter(e, ignore);
			String block = "";
			for (String f : fields) {
				String v = invokeGet(e, f);
				block += f + ": " + v + lineSeparator;
			}
			return block + lineSeparator;
		}).collect(joining());
		return finalText;
	}

	public static <T extends Object> String toCSV(List<T> l,
			List<String> ignore, String sep, boolean header, boolean quote) {
		if (l == null || l.size() == 0) {
			return "";
		}
		String lineSeparator = System.getProperty("line.separator");
		String headerStr = "";
		if (header) {
			headerStr = filter(l.get(0), ignore).stream().map(f -> {
				if (quote) {
					f = "\"" + f + "\"";
				}
				return f;
			}).collect(joining(sep));
			headerStr += lineSeparator;
		}
		String CSV = l.stream().map(e -> {
			List<String> fields = filter(e, ignore);
			String line = "";
			for (String f : fields) {
				String v = invokeGet(e, f);
				if (quote) {
					v = sep + "\"" + v + "\"";
				} else {
					v = sep + v;
				}
				line += v;
			}
			line = line.substring(1, line.length());
			return lineSeparator + line;
		}).collect(joining());
		return headerStr + CSV.substring(1, CSV.length());
	}

	private static <T> List<String> filter(T c, List<String> ignore) {
		return Stream.of(c.getClass().getDeclaredFields()).map(Field::getName)
				.filter(f -> !ignore.contains(f)).collect(toList());
	}

	private static <T> String invokeGet(T c, String field) {
		field = Character.toUpperCase(field.charAt(0))
				+ field.substring(1);
		String mName = "get" + field; 
		try {
			return invokeMethod(c, mName);
		} catch (NoSuchMethodException e) {
			// if it is a boolean
			mName = "is" + field;
		} catch (Exception e) {
			throw new Error(e);
		}
		// try again if it is boolean
		try {
			return invokeMethod(c, mName);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private static <T> String invokeMethod(T c, String mName) throws Error,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Method m = c.getClass().getMethod(mName);
		return String.valueOf(m.invoke(c));

	}

}
