package models;

import java.util.List;

/**
 User: justin.podzimek
 Date: 9/16/15
 */
public class ReferralList {

    private List<Referral> referrals;
    private Integer total;

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Referral> referrals) {
        this.referrals = referrals;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
