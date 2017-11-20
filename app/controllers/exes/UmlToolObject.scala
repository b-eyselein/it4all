package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdPartExToolObject
import model.uml.UmlEnums.UmlExPart
import play.api.mvc.Call

object UmlToolObject extends IdPartExToolObject("uml", "Uml", ToolState.LIVE) {

  override def indexCall: Call = controllers.exes.routes.UmlController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.UmlController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: HasBaseValues) = List(
    (controllers.exes.routes.UmlController.exercise(exercise.id, UmlExPart.CLASS_SELECTION.toString), "Mit Zwischenkorrektur"),
    (controllers.exes.routes.UmlController.exercise(exercise.id, UmlExPart.DIAG_DRAWING.toString), "Freies Erstellen"))

  // Not used anywhere except self reference...
  override def exesListRoute(page: Int): Call = ???

  // not in routes...
  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = ??? // controllers.exes.routes.UmlController.correctLive(exercise.id, part)

  override def correctRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.UmlController.correct(exercise.id, part)


  override val restHeaders: List[String] = List("Klassenwahl", "Diagrammzeichnen", "Lösung", "Mappings", "Ignoriert")

  override def adminIndexRoute: Call = controllers.exes.routes.UmlController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.UmlController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.routes.UmlController.adminNewExerciseForm()

  override def exportExesRoute: Call = controllers.exes.routes.UmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.UmlController.adminExportExercisesAsFile()

  override def importExesRoute: Call = controllers.exes.routes.UmlController.adminImportExercises()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.adminDeleteExercise(exercise.id)

}
