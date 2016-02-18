package model.mindmap.evaluation;

import java.util.HashMap;
import java.util.LinkedList;

public class TupelDifferenceContainer {

	private HashMap<String, LinkedList<Tuple>> mapMissing;
	private HashMap<String, LinkedList<Tuple>> mapWrong;
	private HashMap<String, LinkedList<Tuple>> mapPartiallyCorrect;
	
	public TupelDifferenceContainer(HashMap<String, LinkedList<Tuple>> mapMissing,
			HashMap<String, LinkedList<Tuple>> mapWrong,
			HashMap<String, LinkedList<Tuple>> mapPartiallyCorrect) {
		super();
		this.mapMissing = mapMissing;
		this.mapWrong = mapWrong;
		this.mapPartiallyCorrect = mapPartiallyCorrect;
	}

	public HashMap<String, LinkedList<Tuple>> getMapMissing() {
		return mapMissing;
	}

	public HashMap<String, LinkedList<Tuple>> getMapWrong() {
		return mapWrong;
	}

	public HashMap<String, LinkedList<Tuple>> getMapPartiallyCorrect() {
		return mapPartiallyCorrect;
	}
	
}
