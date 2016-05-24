import java.io.File;
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
public class CorrectorFailTest {

	/**
	 * Test method for
	 * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
	 * 
	 * Failing because of missing closing tag
	 */
	@Test
	public void testCorrectXMLAgainstDTD() {
		File file = new File("test/resources/noteNoClosingTag.xml");
		List<ElementResult> out = CorrectorXml.correctXMLAgainstDTD(file);
		Assert.assertEquals(
				"FATAL ERROR:" + "\n" + "Zeile: 10" + "\n" + "Fehler: "
						+ "The element type \"to\" must be terminated by the matching end-tag \"</to>\".\n",
				out.get(0).getMessage());
	}
}
