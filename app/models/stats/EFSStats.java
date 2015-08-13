package models.stats;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 User: justin.podzimek
 Date: 8/10/15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EFSStats {

    private Integer totalReferrals;
    private Integer totalProductiveReferrals;
    private Integer totalIPS;
    private Integer totalPC;
    private Integer totalInsurance;
    private ProducerCallout mostTotalClients;
    private ProducerCallout mostProductiveReferrals;
    private ProducerCallout highestPercentageProductiveReferrals;

    public Integer getTotalReferrals() {
        return totalReferrals;
    }

    public void setTotalReferrals(Integer totalReferrals) {
        this.totalReferrals = totalReferrals;
    }

    public Integer getTotalProductiveReferrals() {
        return totalProductiveReferrals;
    }

    public void setTotalProductiveReferrals(Integer totalProductiveReferrals) {
        this.totalProductiveReferrals = totalProductiveReferrals;
    }

    public Integer getTotalIPS() {
        return totalIPS;
    }

    public void setTotalIPS(Integer totalIPS) {
        this.totalIPS = totalIPS;
    }

    public Integer getTotalPC() {
        return totalPC;
    }

    public void setTotalPC(Integer totalPC) {
        this.totalPC = totalPC;
    }

    public Integer getTotalInsurance() {
        return totalInsurance;
    }

    public void setTotalInsurance(Integer totalInsurance) {
        this.totalInsurance = totalInsurance;
    }

    public ProducerCallout getMostTotalClients() {
        return mostTotalClients;
    }

    public void setMostTotalClients(ProducerCallout mostTotalClients) {
        this.mostTotalClients = mostTotalClients;
    }

    public ProducerCallout getMostProductiveReferrals() {
        return mostProductiveReferrals;
    }

    public void setMostProductiveReferrals(ProducerCallout mostProductiveReferrals) {
        this.mostProductiveReferrals = mostProductiveReferrals;
    }

    public ProducerCallout getHighestPercentageProductiveReferrals() {
        return highestPercentageProductiveReferrals;
    }

    public void setHighestPercentageProductiveReferrals(ProducerCallout highestPercentageProductiveReferrals) {
        this.highestPercentageProductiveReferrals = highestPercentageProductiveReferrals;
    }
}
