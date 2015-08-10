package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.stats.EFSStats;
import models.Referral;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Date;

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
        stats.setMostTotalClients(Referral.getByMostTotalClients(fromDate, toDate));
        stats.setMostProductiveReferrals(Referral.getByMostProductiveClients(fromDate, toDate));
        stats.setHighestPercentageProductiveReferrals(Referral.getByMostProductiveClientsPercentage(fromDate, toDate));

        // Fill the return data and send it back
        JsonNode referralJson = Json.toJson(stats);
        ObjectNode result = Json.newObject();
        result.put("data", referralJson);
        return ok(result);
    }
}
