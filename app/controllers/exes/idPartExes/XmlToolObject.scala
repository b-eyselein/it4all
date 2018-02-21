package controllers.exes.idPartExes

import model.Consts
import model.core.tools.IdPartExToolObject
import model.xml.{XmlConsts, XmlExPart, XmlExParts, XmlExercise}
import play.api.mvc.Call

import scala.language.postfixOps

object XmlToolObject extends IdPartExToolObject[XmlExPart] {

  override type CompEx = XmlExercise

  override def exParts: Seq[XmlExPart] = XmlExParts.values

  override val hasTags : Boolean = false
  override val toolname: String  = "Xml"
  override val exType  : String  = "xml"
  override val consts  : Consts  = XmlConsts

  override def indexCall: Call = routes.XmlController.index()

  override def exerciseRoute(id: Int, part: String): Call = routes.XmlController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.XmlController.exerciseList(page)

  override def correctLiveRoute(id: Int): Call = routes.XmlController.correctLive(id)

  override def correctRoute(id: Int): Call = routes.XmlController.correct(id)


  override def adminIndexRoute: Call = routes.XmlController.adminIndex()

  override def adminExesListRoute: Call = routes.XmlController.adminExerciseList()

  override def newExFormRoute: Call = routes.XmlController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.XmlController.adminCreateExercise()

  override def importExesRoute: Call = routes.XmlController.adminImportExercises()

  override def exportExesRoute: Call = routes.XmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.XmlController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.XmlController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.XmlController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.XmlController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.XmlController.adminDeleteExercise(id)

}
