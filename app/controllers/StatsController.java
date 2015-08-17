package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserModel;
import models.stats.EFSStats;
import models.Referral;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 User: justin.podzimek
 Date: 8/7/15
 */
public class StatsController extends Controller {

    /**
     Returns EFS stats between the provided timestamps

     @fixme
        This is a heavy method with lots of queries. We should look up a better way to get the results we're after
        without so many database queries.

     @todo
        Add caching.

     @param fromTimestamp Beginning timestamp
     @param toTimestamp End timestamp
     @return EFS stats
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getEFSStats(Long fromTimestamp, Long toTimestamp) {

        // Generate dates from the provided timestamps
        Date fromDate = new Date(fromTimestamp);
        Date toDate = new Date(toTimestamp);

        // Generate the models
        Referral totals = Referral.getTotalsBetweenDates(fromDate, toDate);
        EFSStats stats = new EFSStats();

        // Populate the data
        stats.setTotalReferrals(Referral.getCountBetweenDates(fromDate, toDate));
        stats.setTotalProductiveReferrals(Referral.getProductiveCountBetweenDates(fromDate, toDate));
        stats.setTotalInsurance(totals.gettInsurance());
        stats.setTotalIPS(totals.gettIps());
        stats.setTotalPC(totals.gettPc());
        stats.setMostTotalClients(Referral.getByMostTotalClients(null, fromDate, toDate));
        stats.setMostProductiveReferrals(Referral.getByMostProductiveClients(null, fromDate, toDate));
        stats.setHighestPercentageProductiveReferrals(Referral.getByMostProductiveClientsPercentage(null, fromDate, toDate));

        // Fill the return data and send it back
        JsonNode referralJson = Json.toJson(stats);
        ObjectNode result = Json.newObject();
        result.put("data", referralJson);
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getProducerStats(Long producerId, Long fromTimestamp, Long toTimestamp) {

        // Generate dates from the provided timestamps
        Date fromDate = new Date(fromTimestamp);
        Date toDate = new Date(toTimestamp);

        // Populate the data
        EFSStats stats = new EFSStats();
        stats.setTotalReferrals(Referral.getCountBetweenDates(producerId, fromDate, toDate));
        stats.setTotalProductiveReferrals(Referral.getProductiveCountBetweenDates(producerId, fromDate, toDate));

        // Fill the return data and send it back
        JsonNode referralJson = Json.toJson(stats);
        ObjectNode result = Json.newObject();
        result.put("data", referralJson);
        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getAgentStats(Long agentId, Long fromTimestamp, Long toTimestamp) {

        // Generate dates from the provided timestamps
        Date fromDate = new Date(fromTimestamp);
        Date toDate = new Date(toTimestamp);

        // Get the user and make sure they're an agent.
        UserModel agent = UserModel.getById(agentId);
        if (agent == null || agent.roleType != UserModel.Role.Agent) {
            return notFound(String.format("No agent found matching ID %s", agentId));
        }

        // Get the agent's team members
        List<Long> userIds = agent.getChildTeamMembers().stream().map(childMember -> childMember.id).collect(Collectors.toList());
        List<Referral> referrals = Referral.getByUserIdsBetweenDates(userIds, fromDate, toDate);
        List<Referral> productiveReferrals = referrals.stream().filter(referral -> referral.wasProductive).collect(Collectors.toList());

        // Populate the data
        EFSStats stats = new EFSStats();
        stats.setTotalReferrals(referrals.size());
        stats.setTotalProductiveReferrals(productiveReferrals.size());

        stats.setMostTotalClients(Referral.getByMostTotalClients(userIds, fromDate, toDate));
        stats.setMostProductiveReferrals(Referral.getByMostProductiveClients(userIds, fromDate, toDate));
        stats.setHighestPercentageProductiveReferrals(Referral.getByMostProductiveClientsPercentage(userIds, fromDate, toDate));

        // Fill the return data and send it back
        JsonNode referralJson = Json.toJson(stats);
        ObjectNode result = Json.newObject();
        result.put("data", referralJson);
        return ok(result);
    }
}
