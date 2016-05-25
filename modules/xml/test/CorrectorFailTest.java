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
public class CorrectorFailTest {

	/**
	 * Test method for {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
	 * 
	 * Failing because of missing closing tag
	 */
	@Test
	public void testCorrectXMLAgainstDTD() {
		CorrectorXml corrector = new CorrectorXml();
		File file = new File("/resources/noteNoClosingTag.xml");
		List<ElementResult> out = corrector.correctXMLAgainstDTD(file);
		Assert.assertEqual("FATAL ERROR:"+"\n"+"SystemID: " + /*systemid*/ + "\n" + "Zeile: 3" + "\n" + "Fehler: " + /*error Message*/,out.get(0).getMessage());
	}
}
