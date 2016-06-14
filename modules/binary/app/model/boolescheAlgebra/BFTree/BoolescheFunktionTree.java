package model.boolescheAlgebra.BFTree;

import java.util.List;

public class BoolescheFunktionTree {
	
	private BFKnoten knoten;
	private BF_Variable[] vars;
	
	public BoolescheFunktionTree(BFKnoten k, BF_Variable[] v) {
		this.knoten = k;
		this.vars = v;
	}
	
	/*
	 * Gibt den Wahrheitswert der Funktion zu dem uebergebenen bool Array zurueck.
	 */
	public boolean getWert(boolean[] b) throws IllegalArgumentException{
		if (b.length != this.vars.length) {
			throw new IllegalArgumentException("Wrong number of boolean values. Expected "+this.getAnzahlVariablen()+" elements but there were "+b.length+" elements.");
		}
		for (int i = 0; i < this.vars.length; i++) {
			this.vars[i].setWert(b[i]);
		}
		return this.knoten.getWert();
	}
	
	/*
	 * Gibt boolesche Funktion als Sting zurueck.
	 */
	public String toString() {
		return knoten.toString();
	}
	
	/*
	 * Gibt Wahrheitstafel mit Beschriftung als String zurueck. (geeignet fuer Komandozeile)
	 */
	public String getWahrheitstafelString() {
		String s = "\n ";
		int[] spaltenbreite = new int[this.vars.length+1];
		int spalte = 0;
		for (BF_Variable v : this.vars) {
			s += v.toString()+" | ";
			spaltenbreite[spalte] = s.length()-2;
			spalte++;
		}
		s += this.toString()+"\n";
		spaltenbreite[spalte] = s.length()-2;
		while (spalte > 0) {
			spaltenbreite[spalte] = spaltenbreite[spalte] - spaltenbreite[spalte-1];
			spalte--;
		}
		for (int i = 0; i<spaltenbreite.length; i++) {
			for (int j = 0; j < spaltenbreite[i]-1; j++) {
				s += "-";
			}
			if (i < spaltenbreite.length -1) {
				s += "+";
			} else {
				s += "-\n";
			}
		}
		boolean[] zeile = new boolean[this.vars.length];
		for (int i = 0; i<Math.pow(2, vars.length); i++) {
			for (int j = 0; j<zeile.length; j++) {
				s += " "+booleantochar(zeile[j]);
				for (int h = 2; h<spaltenbreite[j]-1; h++) {
					s += " ";
				}
				s += "|";
			}
			s += " "+booleantochar(this.getWert(zeile)) +"\n";
			int k = vars.length-1;
			if (zeile[vars.length-1]) {
				while (k > 0 && zeile[k]) {
					zeile[k] = false;
					k--;
				}
				zeile[k] = true;
			} else {
				zeile[vars.length-1] = true;
			}
		}
		return s;
	}
	
	/*
	 * Gibt 1 oder 0 zum passenden Wahrheitswert zurueck.
	 */
	private char booleantochar(boolean b) {
		if (b) {
			return '1';
		} else {
			return '0';
		}
	}
	
	/*
	 * Gibt Wahrheitstafel als bool Array zurueck. boolean[Spalte][Zeile] ; Anzahl_der_Spalten = Anzahl_der_Variablen+1 ; Anzahl_der_Zeilen = 2^Anzahl_der_Variablen ;
	 */
	public boolean[][] getWahrheitstafelBoolean() {
		boolean[][] wtafel = new boolean[this.vars.length+1][(int) Math.pow(2,this.vars.length)];
		boolean[] zeile = new boolean[this.vars.length];
		for (int i = 0; i<Math.pow(2, vars.length); i++) {
			for (int j = 0; j<zeile.length; j++) {
				wtafel[j][i] = zeile[j];
			}
			wtafel[this.vars.length][i] = this.getWert(zeile);
			int k = vars.length-1;
			if (zeile[vars.length-1]) {
				while (k > 0 && zeile[k]) {
					zeile[k] = false;
					k--;
				}
				zeile[k] = true;
			} else {
				zeile[vars.length-1] = true;
			}
		}
		return wtafel;
	}
	
	/*
	 *  gibt den Teil der Tabelle der die Belegungen der Variablen enthaelt als Char-Array zurueck. char[Spalte][Zeile] mit '1' fur wahr und '0' fuer falsch
	 */
	public char[][] getVariablenTabelle() {
		char[][] vtafel = new char[this.vars.length][(int) Math.pow(2,this.vars.length)];
		char[] zeile = new char[this.vars.length];
		for (int i = 0; i < zeile.length; i++) {
			zeile[i] = '0';
		}
		for (int i = 0; i<Math.pow(2, vars.length); i++) {
			for (int j = 0; j<zeile.length; j++) {
				vtafel[j][i] = zeile[j];
			}
			int k = vars.length-1;
			if (zeile[vars.length-1] == '1') {
				while (k > 0 && zeile[k] == '1') {
					zeile[k] = '0';
					k--;
				}
				zeile[k] = '1';
			} else {
				zeile[vars.length-1] = '1';
			}
		}
		return vtafel;
	}
	
	/*
	 * gibt sortierten String-Array mit Namen der Variablen zurueck
	 */
	public String[] getVariablen() {
		String[] variablen = new String[vars.length];
		for (int i = 0; i<vars.length; i++) {
			variablen[i] = vars[i].toString();
		}
		return variablen;
	}
	
	/*
	 * gibt Anzahl der Variablen zurueck
	 */
	public int getAnzahlVariablen() {
		return vars.length;
	}
	
	/*
	 * gibt Vector mit den Werten des Ausdrucks zurueck
	 */
	public boolean[] getWahrheitsVector() {
		boolean[] wvector = new boolean[(int) Math.pow(2,this.vars.length)];
		boolean[] zeile = new boolean[this.vars.length];
		for (int i = 0; i<Math.pow(2, vars.length); i++) {
			wvector[i] = this.getWert(zeile);
			int k = vars.length-1;
			if (zeile[vars.length-1]) {
				while (k > 0 && zeile[k]) {
					zeile[k] = false;
					k--;
				}
				zeile[k] = true;
			} else {
				zeile[vars.length-1] = true;
			}
		}
		return wvector;
	}
	
	/*
   * gibt Vector mit den Werten als Char des Ausdrucks zurueck
   */
  public char[] getWahrheitsVectorChar() {
    char[] wvector = new char[(int) Math.pow(2,this.vars.length)];
    boolean[] zeile = new boolean[this.vars.length];
    for (int i = 0; i<Math.pow(2, vars.length); i++) {
      wvector[i] = booleantochar(this.getWert(zeile));
      int k = vars.length-1;
      if (zeile[vars.length-1]) {
        while (k > 0 && zeile[k]) {
          zeile[k] = false;
          k--;
        }
        zeile[k] = true;
      } else {
        zeile[vars.length-1] = true;
      }
    }
    return wvector;
  }
	
	/*
	 * gibt eine Liste der Teilfunktion zurueck einschliesslich sich selbst
	 */
	public List<BoolescheFunktionTree> getTeilformeln() {
		List<BoolescheFunktionTree> teilformeln = this.knoten.getTeilformeln(vars);
		return teilformeln;
	}
	
	/*
	 * Vergeicht diesen BoolescheFunktionTree mit anderem BoolescheFunktionTree. Wirft Fehler wenn die Variablen nicht uebereinstimmen.
	 */
	public boolean compareBoolscheFormelTree(BoolescheFunktionTree otherBFT) throws IllegalArgumentException {
		
		if (this.getAnzahlVariablen() != otherBFT.getAnzahlVariablen()) {
			throw new IllegalArgumentException("Wrong number of vars. Exspected "+this.getAnzahlVariablen()+" but was "+otherBFT.getAnzahlVariablen()+".");
		}
		String[] thisVars = this.getVariablen();
		String[] otherVars = otherBFT.getVariablen();
		for (int i = 0; i<thisVars.length; i++) {
			if (!thisVars[i].equals(otherVars[i])) {
				throw new IllegalArgumentException("Diffrenent variables: "+thisVars[i]+" != "+otherVars[i]+".");
			}
		}
		boolean[] thisWahrheitsVector = this.getWahrheitsVector();
		boolean[] otherWahrheitsVector = otherBFT.getWahrheitsVector();
		for (int i = 0; i<thisWahrheitsVector.length; i++) {
			if (thisWahrheitsVector[i] ^ otherWahrheitsVector[i]) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Vergleicht getWahrheitsVector() mit dem uebergebenen WahrheitsVector (boolean-Array). Wirft Fehler wenn Vektoren (Arrays) unterschiedlich lang sind.
	 */
	public boolean compareBooleanArray(boolean[] otherBooleanArray) throws IllegalArgumentException{
		boolean[] thisBooleanArray = this.getWahrheitsVector();
		if (thisBooleanArray.length != otherBooleanArray.length) {
			throw new IllegalArgumentException("Array has wrong number of elements. Should have " +thisBooleanArray.length +" elements but has " +otherBooleanArray.length +" elements.");
		}
		for (int i = 0; i<thisBooleanArray.length; i++) {
			if (thisBooleanArray[i] ^ otherBooleanArray[i]) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Vergleicht getWahrheitsVector() mit dem uebergebenen WahrheitsVector (String-Array). Wirft Fehler wenn Vektoren (Arrays) unterschiedlich lang sind und wenn der String-Array ungueltige Zeichen enthaelt.
	 */
	public boolean compareStringArray(String[] otherStringArray) throws IllegalArgumentException{
		boolean[] thisWahrheitsVector = this.getWahrheitsVector();
		if (thisWahrheitsVector.length != otherStringArray.length) {
			throw new IllegalArgumentException("Array has wrong number of elements. Should have " +thisWahrheitsVector.length +" elements but has " +otherStringArray.length +" elements.");
		}
		boolean[] otherWahrheitsVector = new boolean[otherStringArray.length];
		for (int i = 0; i<otherStringArray.length; i++) {
			if (otherStringArray[i].trim().equals("1")) {
				otherWahrheitsVector[i] = true;
			} else if (otherStringArray[i].trim().equals("0")) {
				otherWahrheitsVector[i] = false;
			} else {
				throw new IllegalArgumentException("at line "+(i+1)+" unknown value: \""+otherStringArray[i]+"\"");
			}
		}
		for (int i = 0; i<thisWahrheitsVector.length; i++) {
			if (thisWahrheitsVector[i] ^ otherWahrheitsVector[i]) {
				return false;
			}
		}
		return true;
	}
	
}
