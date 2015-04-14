package models;

import java.util.Comparator;

/**
 * User: grant.mills
 * Date: 4/13/15
 * Time: 10:06 AM
 */
public interface HistoryRecord extends Comparable<HistoryRecord> {
	public Long getDateOfLastInteraction();

	public String getDateOfLastInteractionString();

	public String getRecordType();

	public String getStatus();

	public String getLink();

	public int compareTo(HistoryRecord o);
}
