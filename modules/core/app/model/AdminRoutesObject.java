package model;

import play.api.mvc.Call;

public class AdminRoutesObject {

  public final Call exercisesCall;

  public Call newExerciseFormCall;

  public Call exportCall;

  public Call importCall;

  public final Call jsonSchemaCall;

  public final Call uploadCall;

  public AdminRoutesObject(Call theExercisesCall, Call theNewExerciseFormCall, Call theExportCall, Call theImportCall,
      Call theJsonSchemaCall, Call theUploadCall) {
    exercisesCall = theExercisesCall;
    newExerciseFormCall = theNewExerciseFormCall;
    exportCall = theExportCall;
    importCall = theImportCall;
    jsonSchemaCall = theJsonSchemaCall;
    uploadCall = theUploadCall;
  }

}
