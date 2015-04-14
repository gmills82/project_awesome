package utils;

/**
 * User: grant.mills
 * Date: 4/14/15
 * Time: 11:03 AM
 */
public class StatTotals {
	private Integer sumInsurance;
	private Integer sumIps;
	private Integer sumPcs;

	public StatTotals() {
	}

	public StatTotals(Integer sumInsurance, Integer sumIps, Integer sumPcs) {
		this.sumInsurance = sumInsurance;
		this.sumIps = sumIps;
		this.sumPcs = sumPcs;
	}

	public Integer getSumInsurance() {
		return sumInsurance;
	}

	public void setSumInsurance(Integer sumInsurance) {
		this.sumInsurance = sumInsurance;
	}

	public Integer getSumIps() {
		return sumIps;
	}

	public void setSumIps(Integer sumIps) {
		this.sumIps = sumIps;
	}

	public Integer getSumPcs() {
		return sumPcs;
	}

	public void setSumPcs(Integer sumPcs) {
		this.sumPcs = sumPcs;
	}
}
