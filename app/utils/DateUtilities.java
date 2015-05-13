package utils;

import java.text.SimpleDateFormat;

/**
 * User: grant.mills
 * Date: 5/13/15
 * Time: 10:18 AM
 */
public class DateUtilities {
	private static final String dateFormat = "yyyy-MM-dd";
	private static SimpleDateFormat instance = null;

	public static SimpleDateFormat getDateFormat() {
		if(instance == null) {
			instance = new SimpleDateFormat(dateFormat);
		}
		return instance;
	}
}
