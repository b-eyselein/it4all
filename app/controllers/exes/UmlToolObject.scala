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

  override def adminExesListRoute: Call = controllers.exes.routes.UmlController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.UmlController.newExerciseForm()

  override def exportExesRoute: Call = controllers.exes.routes.UmlController.exportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.UmlController.exportExercisesAsFile()

  override def importExesRoute: Call = controllers.exes.routes.UmlController.importExercises()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.UmlController.deleteExercise(exercise.id)

}
