/**
 * 
 */
package test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import model.XmlCorrector;

/**
 * @author rav
 *
 */
public class XmlCorrectorTest {

	/**
	 * Test method for {@link model.XmlCorrector#XmlCorrector()}.
	 */
	@Test
	public void testXmlCorrector() {
		XmlCorrector corrector = new XmlCorrector();
		Assert.assertNotNull(corrector.getFactory());
		Assert.assertNotNull(corrector.getBuilder());
	}

	/**
	 * Test method for {@link model.XmlCorrector#dtdToXml(java.io.File, java.io.File)}.
	 */
	@Test
	public void testDtdToXml() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for {@link model.XmlCorrector#xmlToDTD(java.io.File)}.
	 */
	@Test
	public void testXmlToDTD() {
		XmlCorrector corrector = new XmlCorrector();

		File file = new File("/resources/noteDTD.xml");
		String out = corrector.xmlToDTD(file);
		Assert.assertEquals("", out);
	}

	/**
	 * Test method for {@link model.XmlCorrector#xmlToXSD(java.io.File, java.io.File)}.
	 */
	@Test
	public void testXmlToXSD() {
		XmlCorrector corrector = new XmlCorrector();
		File xml = new File("/resources/noteXML.xml");
		File xsd = new File("/resources/note.xsd");
		String out = new String();
		try {
			out = corrector.xmlToXSD(xsd, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(out);
		Assert.assertNotNull(out);
		Assert.assertEquals("Keine Fehler", out);
	}

}
