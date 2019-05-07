/**
 * 
 */
package org.opentravel.common;

import javafx.util.converter.IntegerStringConverter;

/**
 * Catches number format errors when converting from string to Integer. On error, a 0 is returned.
 * 
 * @author dmh
 *
 */
public class DexRepeatMaxConverter extends IntegerStringConverter {
	@Override
	public Integer fromString(String value) {
		// If the specified value is null or zero-length, return null
		if (value == null) {
			return null;
		}

		value = value.trim();

		if (value.length() < 1) {
			return null;
		}

		Integer i = 0;
		try {
			i = Integer.valueOf(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (i < 0)
			i = 0;
		return i;
		// return Integer.valueOf(value);
	}

	/** {@inheritDoc} */
	@Override
	public String toString(Integer value) {
		// If the specified value is null, return a zero-length String
		if (value == null) {
			return "";
		}
		// Let 0 and negative numbers indicate no displayed value
		if (value < 0)
			return "";
		if (value == 0)
			return "1";
		return (Integer.toString(value.intValue()));
	}

}
