package controllers.idExes

import model.xml.{XmlConsts, XmlExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object XmlToolObject extends IdExToolObject {

  override type CompEx = XmlExercise

  override val hasTags : Boolean = true
  override val toolname: String  = "Xml"
  override val exType  : String  = "xml"
  override val consts  : Consts  = XmlConsts

  override def indexCall: Call = controllers.idExes.routes.XmlController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.exercise(exercise.id)

  override def exerciseListRoute(page: Int): Call = controllers.idExes.routes.XmlController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.correct(exercise.id)


  override val restHeaders: List[String] = List("Typ", "Wurzelknoten", "Inhalt der Referenzdatei")

  override def adminIndexRoute: Call = controllers.idExes.routes.XmlController.adminIndex()

  override def adminExesListRoute: Call = controllers.idExes.routes.XmlController.adminExerciseList()

  override def newExFormRoute: Call = controllers.idExes.routes.XmlController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.idExes.routes.XmlController.adminImportExercises()

  override def exportExesRoute: Call = controllers.idExes.routes.XmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.idExes.routes.XmlController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.XmlController.adminDeleteExercise(exercise.id)

}
