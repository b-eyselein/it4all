package model.boolscheAlgebra.BFTree;

import java.util.List;

public class BoolscheFormelTree {
	
	private BFKnoten knoten;
	private BF_Variable[] vars;
	
	public BoolscheFormelTree(BFKnoten k, BF_Variable[] v) {
		this.knoten = k;
		this.vars = v;
	}
	
	/*
	 * Gibt den Wahrheitswert der Formel zu dem uebergebenen bool Array zurueck.
	 */
	public boolean getWert(boolean[] b) {
		if (b.length != this.vars.length) {
			throw new IllegalArgumentException("wrong number of boolean values");
		}
		for (int i = 0; i < this.vars.length; i++) {
			this.vars[i].setWert(b[i]);
		}
		return this.knoten.getWert();
	}
	
	/*
	 * Gibt boolsche Formel als Sting zurueck.
	 */
	@Override
	public String toString() {
		return knoten.toString();
	}
	
	/*
	 * Gibt Wahrheitstafel mit Beschriftung als String zurueck.
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
				System.out.print(" "+wtafel[j][i]);
			}
			wtafel[this.vars.length][i] = this.getWert(zeile);
			System.out.println(" "+wtafel[this.vars.length][i]);
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
	 * gibt eine Liste der Teilformeln zurueck einschliesslich sich selbst
	 */
	public List<BoolscheFormelTree> getTeilformeln() {
		List<BoolscheFormelTree> teilformeln = this.knoten.getTeilformeln(vars);
		return teilformeln;
	}
}
