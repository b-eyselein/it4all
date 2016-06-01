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
public class NoRoot {

	/**
	 * Test method for
	 * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
	 */
	@Test
	public void testCorrectXMLAgainstDTD() {
		File file = new File("test/resources/partyNoRoot.xml");
		List<ElementResult> out = CorrectorXml.correctXMLAgainstDTD(file);
		Assert.assertTrue(out.size() == 2);
		Assert.assertEquals(
				"ERROR:" + "\n" + "Zeile: 3" + "\n" + "Fehler: "
						+ "Document root element \"gast\", must match DOCTYPE root \"party\".\n",
				out.get(0).getMessage());
		Assert.assertEquals(
				"FATAL ERROR:" + "\n" + "Zeile: 8" + "\n" + "Fehler: "
						+ "The markup in the document following the root element must be well-formed.\n",
				out.get(1).getMessage());
	}

	/**
	 * Test method for
	 * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
	 * .
	 */
	@Test
	public void testCorrectXMLAgainstXSD() {
		File file = new File("test/resources/noteNoRoot.xml");
		File xsd = new File("test/resources/note.xsd");
		List<ElementResult> out = null;
		try {
			out = CorrectorXml.correctXMLAgainstXSD(xsd, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(out.size() == 2);
		Assert.assertEquals("ERROR:" + "\n" + "Zeile: 2" + "\n" + "Fehler: "
				+ "cvc-elt.1.a: Cannot find the declaration of element 'to'.\n", out.get(0).getMessage());
		Assert.assertEquals("FATAL ERROR:" + "\n" + "Zeile: 3" + "\n" + "Fehler: "
				+ "The markup in the document following the root element must be well-formed.\n",out.get(1).getMessage());
	}

}
