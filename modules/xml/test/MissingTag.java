import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import model.CorrectorXml;
import model.ElementResult;

/**
 * 
 */

/**
 * @author rav
 *
 */
public class MissingTag {

	/**
	 * Test method for
	 * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
	 */
	@Test
	public void testCorrectXMLAgainstDTD() {
		File file = new File("test/resources/partyMissingAttribute.xml");
		List<ElementResult> out = CorrectorXml.correctXMLAgainstDTD(file);
		Assert.assertTrue(out.size() == 1);
		Assert.assertEquals(
				"ERROR:" + "\n" + "Zeile: 9" + "\n" + "Fehler: "
						+ "Attribute \"name\" is required and must be specified for element type \"gast\".\n",
				out.get(0).getMessage());
	}

	/**
	 * Test method for
	 * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
	 * .
	 */
	@Test
	public void testCorrectXMLAgainstXSD() {
		File file = new File("test/resources/noteMissingTag.xml");
		File xsd = new File("test/resources/note.xsd");
		List<ElementResult> out = null;
		try {
			out = CorrectorXml.correctXMLAgainstXSD(xsd, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(out.size() == 1);
		Assert.assertEquals(
				"ERROR:" + "\n" + "Zeile: 5" + "\n" + "Fehler: "
						+ "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.\n",
				out.get(0).getMessage());
	}

}
