/**
 * 
 */
package org.opentravel.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opentravel.schemacompiler.validate.FindingMessageFormat;
import org.opentravel.schemacompiler.validate.FindingType;
import org.opentravel.schemacompiler.validate.ValidationFinding;
import org.opentravel.schemacompiler.validate.ValidationFindings;

/**
 * @author dmh
 *
 */
public class ValidationUtils {
	private static Log log = LogFactory.getLog(ValidationUtils.class);

	private ValidationUtils() {
		// NO-OP - static methods only. Do not instantiate this class.
	}

	/**
	 * Get a string with warningCount/errorCount format.
	 * 
	 * @param findings
	 * @return
	 */
	public static String getCountsString(ValidationFindings findings) {
		String errMsg = "-/-";
		if (findings != null) {
			int warnings = findings.count(FindingType.WARNING);
			int errors = findings.count(FindingType.ERROR);
			errMsg = Integer.toString(warnings) + "/" + Integer.toString(errors);
		}
		return errMsg;
	}

	public static String getMessagesAsString(ValidationFindings findings) {
		// log.debug("formatting " + findings.count() + " findings.");
		StringBuilder messages = new StringBuilder();
		if (findings != null)
			findings.getAllFindingsAsList().forEach(
					f -> messages.append(f.getFormattedMessage(FindingMessageFormat.MESSAGE_ONLY_FORMAT) + "\n"));
		return messages.toString();
	}

	/**
	 * Trim key to leave last dot and remainder of key
	 * 
	 * @param key
	 * @return
	 */
	private static String trim(String key) {
		return key.substring(key.lastIndexOf('.'), key.length());
	}

	public static ValidationFindings getRelevantFindings(String[] keyArray, ValidationFindings findings) {
		List<String> longKeys = new ArrayList<>(Arrays.asList(keyArray));
		List<String> keys = new ArrayList<>();
		longKeys.forEach(k -> keys.add(trim(k)));

		ValidationFindings relevant = new ValidationFindings();
		if (findings != null && !findings.isEmpty())
			for (ValidationFinding f : findings.getAllFindingsAsList()) {
				String key = trim(f.getMessageKey());
				if (keys.contains(key))
					relevant.addFinding(f);
				else
					log.debug("Unrelevant Finding: " + key);
				// String msg = f.getFormattedMessage(FindingMessageFormat.IDENTIFIED_FORMAT);
				// String msg2 = f.getFormattedMessage(FindingMessageFormat.MESSAGE_ONLY_FORMAT);
				// FindingType type = f.getType();
				// Validatable source = f.getSource();
				// log.debug("Finding: " + msg2);
			}
		return relevant;
	}

}
