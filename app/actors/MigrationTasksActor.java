package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import models.MigrationTask;
import models.Referral;
import models.ReferralNote;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import utils.DateUtilities;

/**
 An actor that runs on startup to perform any migration tasks that are waiting in the queue

 User: justin.podzimek
 Date: 9/18/15
 */
public class MigrationTasksActor extends UntypedActor {

    private static final String REFERRAL_NEXT_STEPS_TASK = "next-steps-migration";
    private static final String REFERRAL_NOTES_TASK = "referral-notes-migration";

    public static Props props = Props.create(MigrationTasksActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
        // ...
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        if (MigrationTask.getByTaskName(REFERRAL_NOTES_TASK) == null) {
            try {
                runReferralNotesMigration();
                new MigrationTask(REFERRAL_NOTES_TASK).save();
            } catch (Exception e) {
                Logger.error("Error migrating referral notes: " + e);
            }
        }

        if (MigrationTask.getByTaskName(REFERRAL_NEXT_STEPS_TASK) == null) {
            try {
                runNextStepsTimestampMigration();
                new MigrationTask(REFERRAL_NEXT_STEPS_TASK).save();
            } catch (Exception e) {
                Logger.error("Error migrating next steps timestamp: " + e);
            }
        }
    }

    /**
     Loops over all referrals and, if they have an attached note, creates a new ReferralNote. This will be the start
     of allowing a list of notes per referral. Unfortunately, we have no way of knowing who was the writer of the original
     note, so the creator will be null for this migration.
     */
    private void runReferralNotesMigration() {
        for (Referral referral : Referral.getAll()) {

            // If the referral has no notes, we don't need to migrate anything
            if (StringUtils.trimToNull(referral.getRefNotes()) == null) {
                continue;
            }

            ReferralNote note = new ReferralNote();
            note.setCreatedDate(DateUtilities.normalizeDateString(referral.getDateOfLastInteractionString()));
            note.setReferralId(referral.id);
            note.setNote(referral.getRefNotes());
            note.save();

            referral.setRefNotes(null);
//            referral.update();
        }
    }


    private void runNextStepsTimestampMigration() {
        for (Referral referral : Referral.getAll()) {
            referral.setNextStepDate(referral.getNextStepDate());
            referral.update();
        }
    }
}
