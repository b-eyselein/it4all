package controllers.xml;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DTDAgainstXML {
	
	public static void main(String[] args) throws IOException {

		FileInputStream fstream1 = new FileInputStream("//home//shpend//Downloads//party2.dtd");
		FileInputStream fstream2 = new FileInputStream("//home//shpend//Downloads//party.dtd" );

		DataInputStream in1 = new DataInputStream(fstream1);
		DataInputStream in2 = new DataInputStream(fstream2);

		BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

		String strLine1, strLine2 = null;
		List<String> output = new ArrayList<>();
		List<String> output2 = new ArrayList<>();
		int lines = 0;

		while ((strLine1 = br1.readLine()) != null && (strLine2 = br2.readLine()) != null) {
			lines++;
			if (!strLine1.equals(strLine2)) {
				output.add(strLine1 + "\n" + "Zeile: " + lines);
				output2.add(strLine2);
			}
		}
		if (!output.isEmpty()) {
			for (String string : output) {
				System.out.println("But was: " + string + "\n");
			}
		}

	}

}
