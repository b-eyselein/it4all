import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import model.CorrectorXml;

/**
 * 
 */

/**
 * @author rav
 *
 */
public class CorrectorTest {

	/**
	 * Test method for {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
	 */
	@Test
	public void testCorrectXMLAgainstDTD() {
		CorrectorXml corrector = new CorrectorXml();
		File file = new File("/resources/noteDTD.xml");
		List<String> out = corrector.correctXMLAgainstDTD(file);
		Assert.assertTrue(out.isEmpty());
	}

	/**
	 * Test method for {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}.
	 */
	@Test
	public void testCorrectXMLAgainstXSD() {
		CorrectorXml corrector = new CorrectorXml();
		File xml = new File("/resources/note.xml");
		File xsd = new File("/resources/note.xsd");
		List<String> out = null;
		try {
			out = corrector.correctXMLAgainstXSD(xsd, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(out);
		Assert.assertTrue(out.isEmpty());
	}

	/**
	 * Test method for {@link model.CorrectorXml#correctDTDAgainstXML(java.io.File)}.
	 */
	@Test
	public void testCorrectDTDAgainstXML() {
		fail("Not yet implemented"); // TODO
	}

}
