package controllers;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;
import java.util.List;

/**
 * User: grant.mills
 * Date: 4/24/15
 * Time: 7:21 PM
 */
public class PDFController extends Controller {
	public static Result newAccount(Long clientId) {
		try {
			PDDocument document = PDDocument.load("pdf/new-account.pdf");
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			//View parts of document
			PDAcroForm form = document.getDocumentCatalog().getAcroForm();
			List fields = form.getFields();

			for(Object field : fields) {
				PDField pdfield = (PDField) field;
				Logger.debug(pdfield.getFullyQualifiedName() + " - " + pdfield.getFieldType() + " - " + pdfield.getActions().toString());
			}

			PDField btnField = form.getField("inittran1");
			((PDCheckbox) btnField).check();

			PDField textField = form.getField("other");

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
}