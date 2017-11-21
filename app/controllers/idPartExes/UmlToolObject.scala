package controllers.idPartExes

import model.uml.UmlEnums.UmlExPart
import model.uml.{UmlCompleteEx, UmlConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object UmlToolObject extends IdPartExToolObject {

  override type CompEx = UmlCompleteEx

  override val hasTags : Boolean = false
  override val toolname: String  = "Uml"
  override val exType  : String  = "uml"
  override val consts  : Consts  = UmlConsts

  override def indexCall: Call = controllers.idPartExes.routes.UmlController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.idPartExes.routes.UmlController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: UmlCompleteEx) = List(
    (controllers.idPartExes.routes.UmlController.exercise(exercise.ex.id, UmlExPart.CLASS_SELECTION.toString), "Mit Zwischenkorrektur"),
    (controllers.idPartExes.routes.UmlController.exercise(exercise.ex.id, UmlExPart.DIAG_DRAWING.toString), "Freies Erstellen"))

  override def exerciseListRoute(page: Int): Call = controllers.idPartExes.routes.UmlController.exerciseList(page)

  // not in routes...
  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = ??? // controllers.exes.routes.UmlController.correctLive(exercise.id, part)

  override def correctRoute(exercise: HasBaseValues, part: String): Call = controllers.idPartExes.routes.UmlController.correct(exercise.id, part)


  override val restHeaders: List[String] = List("Klassenwahl", "Diagrammzeichnen", "Lösung", "Mappings", "Ignoriert")

  override def adminIndexRoute: Call = controllers.idPartExes.routes.UmlController.adminIndex()

  override def adminExesListRoute: Call = controllers.idPartExes.routes.UmlController.adminExerciseList()

  override def newExFormRoute: Call = controllers.idPartExes.routes.UmlController.adminNewExerciseForm()

  override def exportExesRoute: Call = controllers.idPartExes.routes.UmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.idPartExes.routes.UmlController.adminExportExercisesAsFile()

  override def importExesRoute: Call = controllers.idPartExes.routes.UmlController.adminImportExercises()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.UmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.UmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.UmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.UmlController.adminDeleteExercise(exercise.id)

}
