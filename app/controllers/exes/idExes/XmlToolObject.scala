package controllers.exes.idExes

import model.xml.{XmlConsts, XmlExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object XmlToolObject extends IdExToolObject {

  override type CompEx = XmlExercise

  override val hasTags : Boolean = true
  override val toolname: String  = "Xml"
  override val exType  : String  = "xml"
  override val consts  : Consts  = XmlConsts

  override def indexCall: Call = routes.XmlController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = routes.XmlController.exercise(exercise.id)

  override def exerciseListRoute(page: Int): Call = routes.XmlController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = routes.XmlController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = routes.XmlController.correct(exercise.id)


  override def adminIndexRoute: Call = routes.XmlController.adminIndex()

  override def adminExesListRoute: Call = routes.XmlController.adminExerciseList()

  override def newExFormRoute: Call = routes.XmlController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.XmlController.adminCreateExercise()

  override def importExesRoute: Call = routes.XmlController.adminImportExercises()

  override def exportExesRoute: Call = routes.XmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.XmlController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.XmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.XmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.XmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.XmlController.adminDeleteExercise(exercise.id)

}
