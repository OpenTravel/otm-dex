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
import org.opentravel.schemacompiler.validate.Validatable;
import org.opentravel.schemacompiler.validate.ValidationFinding;
import org.opentravel.schemacompiler.validate.ValidationFindings;

/**
 * @author dmh
 *
 */
public class ValidationUtils {
	private static Log log = LogFactory.getLog(ValidationUtils.class);

	public static String getMessagesAsString(ValidationFindings findings) {
		log.debug("formatting " + findings.count() + " findings.");
		StringBuilder messages = new StringBuilder();
		findings.getAllFindingsAsList()
				.forEach(f -> messages.append(f.getFormattedMessage(FindingMessageFormat.MESSAGE_ONLY_FORMAT) + "\n"));
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

	// TODO - move to validation utils
	public static ValidationFindings getRelevantFindings(String[] KEYS, ValidationFindings findings) {
		List<String> longKeys = new ArrayList<>(Arrays.asList(KEYS));
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
				String msg = f.getFormattedMessage(FindingMessageFormat.IDENTIFIED_FORMAT);
				String msg2 = f.getFormattedMessage(FindingMessageFormat.MESSAGE_ONLY_FORMAT);
				FindingType type = f.getType();
				Validatable source = f.getSource();
				log.debug("Finding: " + msg2);
			}
		return relevant;
	}

}
