package controllers;

import models.Client;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.AddressUtilities;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: grant.mills
 * Date: 4/24/15
 * Time: 7:21 PM
 */
public class PDFController extends Controller {

	public static final String PAOLEGADD = "paolegadd";
	public static final String PAOLEGST = "paolegst";
	public static final String PAOLEGCITY = "paolegcity";
	public static final String PAOLEGZIP = "paolegzip";
	public static final String PAOMAILADDR = "paomailaddr";
	public static final String PAOMAILST = "paomailst";
	public static final String PAOMAILCITY = "paomailcity";
	public static final String PAOMAILZIP = "paomailzip";
	public static final String PAOHOMEPH = "paohomeph";
	public static final String PAODOB = "paodob";

	public static Result newAccount(Long clientId) {
		Client client = Client.getById(clientId);

		try {
			PDDocument document = PDDocument.load("pdf/new-account.pdf");
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			Map<String, String> clientFieldMap = prepFieldMap(client);
			fillForm(document, clientFieldMap);

			document.save(output);
			document.close();
			response().setContentType("application/pdf");
			return ok(output.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return internalServerError();
		} catch (IOException e) {
			e.printStackTrace();
			return internalServerError();
		} catch (COSVisitorException e) {
			e.printStackTrace();
			return internalServerError();
		}
	}

	private static void fillForm(PDDocument document, Map<String, String> clientFieldMap) {

		//View parts of document
		PDAcroForm form = document.getDocumentCatalog().getAcroForm();

		//Debug to list all fields in form
		List fields = null;
		try {
			fields = form.getFields();
			for(Object field : fields) {
				PDField pdfield = (PDField) field;
				Logger.debug(pdfield.getFullyQualifiedName() + " - " + pdfield.getFieldType() + " - " + pdfield.findFieldType());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Insert info into fields
		Iterator it = clientFieldMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, String> next = (Map.Entry<String, String>)it.next();
			PDField textField = null;
			try {
				textField = form.getField(next.getKey());
				//Workaround because of zero font size bug in PDFBox
				if(null != next.getValue()) {
					COSString fieldValue = new COSString(next.getValue());
					textField.getDictionary().setItem(COSName.V, fieldValue);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//Example for checkbox
//			PDField btnField = form.getField("inittran1");
//			((PDCheckbox) btnField).check();

	}

	private static Map<String, String> prepFieldMap(Client client) {
		Map<String, String> clientFieldMap = new HashMap<String, String>();
		String stateNameAbbrev = convertToStateAbbrev(client.state);

		//Name
		clientFieldMap.put("paoname", client.name);
		clientFieldMap.put("fslpfrname", client.name);
		//Legal Address
		clientFieldMap.put(PAOLEGADD, client.address1);
		clientFieldMap.put(PAOLEGST, stateNameAbbrev);
		clientFieldMap.put(PAOLEGCITY, client.city);
		clientFieldMap.put(PAOLEGZIP, client.zipcode);
		//Mailing address
		clientFieldMap.put(PAOMAILADDR, client.address1);
		clientFieldMap.put(PAOMAILST, stateNameAbbrev);
		clientFieldMap.put(PAOMAILCITY, client.city);
		clientFieldMap.put(PAOMAILZIP, client.zipcode);
		//Phone
		clientFieldMap.put(PAOHOMEPH, client.phoneNumber);
		//DOB
		clientFieldMap.put(PAODOB, client.birthDatePretty);

		return clientFieldMap;
	}

	private static String convertToStateAbbrev(String stateName) {
		String stateAbbrev = null;
		if(null != stateName) {
			stateAbbrev = AddressUtilities.STATE_MAP.get(WordUtils.capitalizeFully(stateName));
		}
		return stateAbbrev;
	}

}