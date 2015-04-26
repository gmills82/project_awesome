package controllers;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;

/**
 * User: grant.mills
 * Date: 4/24/15
 * Time: 7:21 PM
 */
public class PDFController extends Controller {
	public static Result newAccount(Long clientId) {
		try {
			InputStream fileStream = new FileInputStream("pdf/new-account.pdf");
			PDFParser parser = new PDFParser(fileStream);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			parser.parse();
			PDDocument document = parser.getPDDocument();
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