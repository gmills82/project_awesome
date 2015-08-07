package utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import play.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     Formats a date into a common string pattern

     @param date Date to format
     @return Formatted date
     */
    public static String getFormattedDate(Date date) {
        return getDateFormat().format(date);
    }

    /**
     There are a few different date string formats out in the wild. This attempts to parse the bad, and return the closest
     equal value in a valid date object. If unable to convert, today's date will be provided.

     @param dateString Date string
     @return Date object
     */
	public static Date normalizeDateString(String dateString) {
        Date date = null;
        Boolean hasAmPmDefined = true;

        // No date string provided?
        if (StringUtils.stripToNull(dateString) == null) {
            return new Date();
        }

        // 2015-07-28 01:00 AM
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm aa").parse(dateString);
        } catch (ParseException e) {
            // ...
        }

        // 2015-07-24 05:30
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(dateString);
                hasAmPmDefined = false;
            } catch (ParseException e) {
                // ...
            }
        }

        // 2015-07-06 01
        if (date == null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd hh").parse(dateString);
                hasAmPmDefined = false;
            } catch (ParseException e) {
                // ...
            }
        }

        // 2015-07-06 undefined
        if (date == null) {
            try {

                // Zero out the hours and minutes
                date = new DateTime(
                        new SimpleDateFormat("yyyy-MM-dd").parse(dateString)
                ).withHourOfDay(0).withMinuteOfHour(0).toDate();
                hasAmPmDefined = false;

            } catch (ParseException e) {
                // ...
            }
        }

        // 07/28/15
        if (date == null) {
            try {

                // Zero out the hours and minutes
                date = new DateTime(
                        new SimpleDateFormat("MM/dd/yy").parse(dateString)
                ).withHourOfDay(0).withMinuteOfHour(0).toDate();
                hasAmPmDefined = false;

            } catch (ParseException e) {
                // ...
            }
        }

        // 07/28/2015
        if (date == null) {
            try {

                // Zero out the hours and minutes
                date = new DateTime(
                        new SimpleDateFormat("MM/dd/yyyy").parse(dateString)
                ).withHourOfDay(0).withMinuteOfHour(0).toDate();
                hasAmPmDefined = false;

            } catch (ParseException e) {
                // ...
            }
        }

        // Fallback to prevent an NPE
        if (date == null) {
            date = new Date();
            Logger.warn("Unable to convert date string " + dateString + " into a valid date object. Falling back to today.");
        }

        // A little housecleaning
        if (!hasAmPmDefined) {
            DateTime temporaryDate = new DateTime(date);

            // If there's no AM/PM defined and the hour is set to before 6, let's assume PM. Nobody likes being woken up
            // before the roosters crow and I doubt the agent would be up at that time anyways.
            if (temporaryDate.getHourOfDay() < 6 && temporaryDate.getHourOfDay() >= 0) {
                temporaryDate = temporaryDate.withHourOfDay(temporaryDate.getHourOfDay() + 12);
            }
            date = temporaryDate.toDate();
        }

        return date;
    }
}
