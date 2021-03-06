package models;

import java.util.Comparator;
import java.util.List;

/**
 * User: grant.mills
 * Date: 4/13/15
 * Time: 10:06 AM
 */
public interface HistoryRecord extends Comparable<HistoryRecord> {

    public long getId();

	public Long getDateOfLastInteraction();

	public String getDateOfLastInteractionString();

	public String getRecordType();

	public String getRecordStatus();

	public String getLink();

//	public String getNotes();

    public List<ReferralNote> getNotes();

	public String getCreatorName();

	public int compareTo(HistoryRecord o);
}
