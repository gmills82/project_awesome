package models.stats;

import models.UserModel;

/**
 User: justin.podzimek
 Date: 8/10/15
 */
public class ProducerCallout {

    public enum CalloutType {
        TOTAL,
        PERCENTACE
    }

    private UserModel user;
    private UserModel supervisor;
    private Float callout;
    private CalloutType calloutType = CalloutType.TOTAL;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(UserModel supervisor) {
        this.supervisor = supervisor;
    }

    public Float getCallout() {
        return callout;
    }

    public void setCallout(Float callout) {
        this.callout = callout;
    }

    public CalloutType getCalloutType() {
        return calloutType;
    }

    public void setCalloutType(CalloutType calloutType) {
        this.calloutType = calloutType;
    }
}
