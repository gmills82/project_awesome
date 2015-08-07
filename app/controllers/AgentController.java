package controllers;

import models.Referral;
import models.UserModel;
import org.apache.poi.hssf.usermodel.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.DateUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 User: justin.podzimek
 Date: 8/2/15
 */
public class AgentController extends Controller {

    /** The max column size for the referral excel sheet */
    private static final int MAX_XLS_COL_WIDTH = 10000;

    /**
     Downloads an excel spreadsheet of referrals for the provided agent and producer. If the producer ID is not defined,
     look up all producers. Each producer in the excel file will have their own sheet populated with their referrals.

     @param agentId Agent ID
     @param producerId Producer ID
     @return Result of download
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result getReferralsByAgentId(Long agentId, Long producerId) {

        // Look up the agent to verify that the provided ID exists. If found, make sure the ID belongs to a user set to
        // the "agent" role type.
        UserModel agent = UserModel.getById(agentId);
        if (agent == null || agent.roleType != UserModel.Role.Agent) {
            return notFound(String.format("No agent found matching the id %s", agentId));
        }

        // If the provided producer ID is null, look up the referrals for all of the producers. If not, only use that ID
        // to look up the referrals. In both cases, look up the referrals for the provided user agent as well. Also, keep
        // a map of users for easier look up later on.
        List<Long> producerIds = new ArrayList<>();
        Map<Long, UserModel> producerMap = new HashMap<>();
        producerIds.add(agent.id);
        producerMap.put(agent.id, agent);
        if (producerId == null || producerId == 0) {
            if (agent.childTeamMembers != null) {
                for (UserModel userModel : agent.childTeamMembers) {
                    producerMap.put(userModel.id, userModel);
                }
                producerIds.addAll(agent.childTeamMembers.stream().map(producer -> producer.id).collect(Collectors.toList()));
            }
        } else {
            producerIds.add(producerId);
            UserModel producer = UserModel.getById(producerId);
            if (producer == null) {
                return notFound(String.format("No producer found matching id %s", producerId));
            }
            producerMap.put(producerId, producer);
        }

        // Get the list of referrals for all of the producers.
        List<Referral> referrals = Referral.getByCreatorIds(producerIds);

        // Make sure we have referrals before going through the process of generating a document.
        if (referrals == null || referrals.size() == 0) {
            return ok(String.format("No referrals found for agent %s", agentId));
        }

        // Generate a map of each producer's referral. This way, we can name each worksheet as the producer name to keep
        // everything organized.
        Map<Long, List<Referral>> producerReferralMap = new HashMap<>();
        for (Referral referral : referrals) {
            if (producerReferralMap.get(referral.user_id) == null) {
                producerReferralMap.put(referral.user_id, new ArrayList<>());
            }
            producerReferralMap.get(referral.user_id).add(referral);
        }

        // Start the output stream and set the content headers for the xls file.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response().setContentType("application/vnd.ms-excel");
        response().setHeader("Content-Disposition", "attachment; filename=referrals.xls");

        // Generate the workbook
        HSSFWorkbook workbook = new HSSFWorkbook();

        // Referral cell titles and style
        List<String> referralTitles = Arrays.asList(
                "Client Name",
                "Status",
                "Next Steps Date",
                "Reason for Referral",
                "Notes",
                "Date Created"
        );
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont cellFont = workbook.createFont();
        cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(cellFont);

        // Loop through each producer, generate a new sheet, and populate the data of the sheet with their referrals. We
        // should have the producer in the map already, but if not (for whatever reason), look them up.
        for (Long producer : producerReferralMap.keySet()) {
            UserModel producerModel = producerMap.get(producer);
            if (producerModel == null) {
                producerModel = UserModel.getById(producer);
            }
            HSSFSheet sheet = workbook.createSheet((producerModel == null) ? "User" : producerModel.getFullName());
            int rowNumber = 0;

            // Create the cell titles
            int titleCellNumber = 0;
            HSSFRow titleRow = sheet.createRow(rowNumber++);
            for (String title : referralTitles) {
                HSSFCell cell = titleRow.createCell(titleCellNumber++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(title);
            }

            // Loop through each of the referrals and populate the data in the sheet.
            for (Referral referral : producerReferralMap.get(producer)) {
                int cellNumber = 0;
                HSSFRow row = sheet.createRow(rowNumber++);
                row.createCell(cellNumber++).setCellValue(referral.getClientName());
                row.createCell(cellNumber++).setCellValue(referral.getStatus());
                row.createCell(cellNumber++).setCellValue(DateUtilities.getFormattedDate(referral.getNextStepTimestamp()));
                row.createCell(cellNumber++).setCellValue(referral.getReasonForReferral());
                row.createCell(cellNumber++).setCellValue(referral.getRefNotes());
                row.createCell(cellNumber++).setCellValue(DateUtilities.getFormattedDate(referral.getCreatedDate()));
            }

            // Loop through the columns one last time after everything has been filled to set the autosize of the column
            // so everything fits nicely.
            for (int i = 0; i < referralTitles.size(); i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > MAX_XLS_COL_WIDTH) {
                    sheet.setColumnWidth(i, MAX_XLS_COL_WIDTH);
                }
            }
        }

        // Write out the workbook to the output stream, close the stream, and return the results.
        try {
            workbook.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(output.toByteArray());
    }
}
