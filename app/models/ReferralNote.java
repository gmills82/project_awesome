package models;

import play.db.ebean.Model;
import utils.DateUtilities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 User: justin.podzimek
 Date: 9/18/15
 */
@Entity
@Table(name = "referral_notes")
public class ReferralNote extends Model {

    /*************************************************************
     PROPERTIES
     ************************************************************/

    /** Primary note ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    /** ID of the referral who the note is for */
    private Long referralId;

    /** ID of the user who made the note */
    private Long userModelId;

    /** The note text */
    private String note;

    /** Date the note was created */
    private Date createdDate;

    /** The user who made the note. This can be null if there is no userModelId */
    @Transient
    private UserModel userModel;

    /** Persistence finder */
    public static Model.Finder<Long, ReferralNote> finder = new Model.Finder<>(Long.class, ReferralNote.class);

    /*************************************************************
     GETTERS & SETTERS
     ************************************************************/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferralId() {
        return referralId;
    }

    public void setReferralId(Long referralId) {
        this.referralId = referralId;
    }

    public Long getUserModelId() {
        return userModelId;
    }

    public void setUserModelId(Long userModelId) {
        this.userModelId = userModelId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    /**
     Returns the created date in the commonly formatted string pattern

     @return String formatted created date
     */
    public String getFormattedCreatedDate() {
        if (this.createdDate == null) {
            return null;
        }
        return DateUtilities.getFormattedDateWithTime(this.createdDate);
    }

    /*************************************************************
     PERSISTENCE
     ************************************************************/

    /**
     Returns the list of notes for the provided referral ID

     @param referralId Referral ID
     @return List of notes
     */
    public static List<ReferralNote> getByReferralId(Long referralId) {
        return finder
                .where()
                .eq("referral_id", referralId)
                .orderBy("created_date DESC")
                .findList();
    }
}
