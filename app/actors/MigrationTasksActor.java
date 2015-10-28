package actors;

import akka.actor.Props;
import akka.actor.UntypedActor;
import models.*;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import utils.DateUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 An actor that runs on startup to perform any migration tasks that are waiting in the queue

 User: justin.podzimek
 Date: 9/18/15
 */
public class MigrationTasksActor extends UntypedActor {

    private static final String REFERRAL_NEXT_STEPS_TASK = "next-steps-migration";
    private static final String REFERRAL_NOTES_TASK = "referral-notes-migration";
    private static final String CLIENT_GROUP_TASK = "client-group";
    private static final String TEAM_GROUP_TASK = "team-group";

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

        if (MigrationTask.getByTaskName(TEAM_GROUP_TASK) == null) {
            try {
                runTeamGroupMigration();
                new MigrationTask(TEAM_GROUP_TASK).save();
            } catch (Exception e) {
                Logger.error("Error migrating team groups: " + e);
            }
        }

        if (MigrationTask.getByTaskName(CLIENT_GROUP_TASK) == null) {
            try {
                runClientGroupMigration();
                new MigrationTask(CLIENT_GROUP_TASK).save();
            } catch (Exception e) {
                Logger.error("Error migrating client groups: " + e);
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
            referral.update();
        }
    }


    private void runNextStepsTimestampMigration() {
        for (Referral referral : Referral.getAll()) {
            referral.setNextStepDate(referral.getNextStepDate());
            referral.update();
        }
    }

    /**
     Looks up all clients, and the users that created them, and assigns them a group ID that matches the group ID of the
     user that created them.
     */
    private void runClientGroupMigration() {

        for (Client client : Client.all()) {

            // Looks like these are unique, why return a list?
            List<Referral> referrals = Referral.getByClientId(client.getId());
            if (referrals == null || referrals.size() == 0) {
                continue;
            }

            Referral referral = referrals.get(0);
            UserModel userModel = UserModel.getById(referral.getCreatorId());
            if (userModel != null) {
                client.setGroupId(Long.valueOf(userModel.getGroupId()));
                client.update();
            }
        }
    }

    /**
     Looks up all users who are EFS, along with each of their children (Agents, Producers, etc.) and assigns them a group
     ID. The group number is the user ID of the original EFS user, so it'll be easy to find which group starts with which
     team member.
     */
    private void runTeamGroupMigration() {

        // We want to start with the top-most member, EFA, and find all children in all levels from that.
        for (UserModel user : UserModel.getByUserRole(UserRole.FA)) {
            Set<UserModel> children = UserModel.getChildUserModelsByParentAllLevels(user);
            if (children == null) {
                children = new HashSet<>();
            }

            // It makes it easier to just add the parent to the group of children. That way, we can loop over all users
            // at once and set their group accordingly. Sure, the terminology is a bit weird with the parent being its
            // own child, but it makes the saving only occur in one place. #whatevs
            Long groupId = user.getId();
            children.add(user);
            for (UserModel child : children) {
                child.setGroupId(groupId.intValue());
                child.update();
            }
        }
    }
}
