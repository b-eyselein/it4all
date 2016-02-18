package model.mindmap.evaluation.enums;

public enum MetaDataState {

	EMPTY, FROM_MINDMAP, FROM_METAFILE;
	
	public static final MetaDataState getMDS(String mDS) {
		switch (mDS.toUpperCase()) {
		case "EMPTY":
			return EMPTY;
		case "FROM_MINDMAP":
			return FROM_MINDMAP;
		case "FROM_METAFILE":
			return FROM_METAFILE;
		default:
			return null;
		}
	}
}
