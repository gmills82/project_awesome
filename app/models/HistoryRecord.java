package models;

import play.db.ebean.Model;

/**
 * User: grant.mills
 * Date: 4/1/15
 * Time: 4:19 PM
 */
public class HistoryRecord extends Model implements Comparable<HistoryRecord> {
	private Long dateOfLastInteraction;

	public void setDateOfLastInteraction(Long dateOfLastInteraction) {
		this.dateOfLastInteraction = dateOfLastInteraction;
	}

	public Long getDateOfLastInteraction() {
		return dateOfLastInteraction;
	}

	@Override
	public int compareTo(HistoryRecord historyRecord) throws ClassCastException {
		
		if(!(historyRecord instanceof HistoryRecord)) {
			throw new ClassCastException("A HistoryRecord object expected");
		}
		long difference = this.getDateOfLastInteraction() - historyRecord.getDateOfLastInteraction();
		int comparator = 0;
		if(difference > 0) {
			comparator = 1;
		}else if(difference < 0) {
			comparator = -1;
		}

		return comparator;
	}
}
