package controllers;

import models.FileFormat;
import models.Referral;
import models.UserModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.api.libs.iteratee.Enumerator;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 User: justin.podzimek
 Date: 8/2/15
 */
public class AgentController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getReferralsByAgentId(Long agentId, Long producerId) {

        // Look up the agent to verify that the provided ID exists. If found, make sure the ID belongs to a user set to
        // the "agent" role type.
        UserModel agent = UserModel.getById(agentId);
        if (agent == null || agent.roleType != UserModel.Role.Agent) {
            return notFound(String.format("No agent found matching the id %s", agentId));
        }

        // If the provided producer ID is null, look up the referrals for all of the producers. If not, only use that ID
        // to look up the referrals. In both cases, look up the referrals for the provided user agent as well.
        List<Long> producerIds = new ArrayList<>();
        producerIds.add(agent.id);
        if (producerId == null || producerId == 0) {
            if (agent.childTeamMembers != null) {
                producerIds.addAll(agent.childTeamMembers.stream().map(producer -> producer.id).collect(Collectors.toList()));
            }
        } else {
            producerIds.add(producerId);
        }

        // Get the list of referrals for all of the producers.
        List<Referral> referrals = Referral.getByCreatorIds(producerIds);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response().setContentType("application/vnd.ms-excel");
        response().setHeader("Content-Disposition", "attachment; filename=filename.xls");

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Sample sheet");

        Map<String, Object[]> data = new HashMap<String, Object[]>();
        data.put("1", new Object[] {"Emp No.", "Name", "Salary"});
        data.put("2", new Object[] {1d, "John", 1500000d});
        data.put("3", new Object[]{2d, "Sam", 800000d});
        data.put("4", new Object[]{3d, "Dean", 700000d});

        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            HSSFRow row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                HSSFCell cell = row.createCell(cellnum++);
                if(obj instanceof Date)
                    cell.setCellValue((Date)obj);
                else if(obj instanceof Boolean)
                    cell.setCellValue((Boolean)obj);
                else if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Double)
                    cell.setCellValue((Double)obj);
            }
        }

        try {
            workbook.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
// ...
// Now populate workbook the usual way.
// ...

        return ok(output.toByteArray());
    }
}
