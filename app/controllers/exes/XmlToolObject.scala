package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object XmlToolObject extends IdExToolObject("xml", "Xml", ToolState.LIVE) {

  override def indexCall: Call = controllers.exes.routes.XmlController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.exercise(exercise.id)

  // only self reference...
  override def exesListRoute(page: Int): Call = ??? //controllers.exes.routes.XmlController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.correct(exercise.id)


  override val restHeaders: List[String] = List("Typ", "Wurzelknoten", "Inhalt der Referenzdatei")

  override def adminIndexRoute: Call = controllers.exes.routes.XmlController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.XmlController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.routes.XmlController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.XmlController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.routes.XmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.XmlController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.adminDeleteExercise(exercise.id)

}
