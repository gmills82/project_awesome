package models;

/**
 User: justin.podzimek
 Date: 10/21/15
 */
public enum ReferralStatus {

    OPEN("OPEN"),
    CLOSED("CLOSED"),
    PROCESSING("PROCESSING"),
    DECLINED("DECLINED");

    private String status;

    ReferralStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     Returns the referral status matching the provided status

     @param status Status
     @return Referral status
     */
    public static ReferralStatus getByStatus(String status) {
        if (status == null) {
            return null;
        }
        for (ReferralStatus referralStatus : ReferralStatus.values()) {
            if (referralStatus.getStatus().equalsIgnoreCase(status.trim())) {
                return referralStatus;
            }
        }
        return null;
    }
}
