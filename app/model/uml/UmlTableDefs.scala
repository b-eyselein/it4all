package model.uml

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.uml.UmlConsts._
import model.{ExerciseState, _}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping])
  extends PartsCompleteEx[UmlExercise, UmlExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.uml.umlPreview(this)

  def titleForPart(part: UmlExPart): String = part match {
    case ClassSelection     => "Auswahl der Klassen"
    case DiagramDrawing     => "Freies Zeichnen"
    case DiagramDrawingHelp => "Modellierung der Beziehungen"
    case MemberAllocation   => "Zuordnung der Member"
  }

  def textForPart(part: UmlExPart): Html = Html(part match {
    case ClassSelection | DiagramDrawing => ex.markedText
    case _                               => ex.text
  })

  override def hasPart(partType: UmlExPart): Boolean = partType match {
    case ClassSelection | DiagramDrawing => true // TODO: Currently deactivated...
    case _                               => false
  }

  def getDefaultClassDiagForPart(part: UmlExPart): UmlClassDiagram = {
    val assocs: Seq[UmlAssociation] = Seq.empty
    val impls: Seq[UmlImplementation] = Seq.empty

    val classes: Seq[UmlClass] = part match {
      case DiagramDrawingHelp => ex.solution.classes.map {
        oldClass => UmlClass(oldClass.classType, oldClass.className, attributes = Seq.empty, methods = Seq.empty, position = oldClass.position)
      }
      case _                  => Seq.empty
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  val allAttributes: Seq[UmlClassMember] = allDistinctMembers(_.attributes)

  val allMethods: Seq[UmlClassMember] = allDistinctMembers(_.methods)

  private def allDistinctMembers(members: UmlClass => Seq[UmlClassMember]): Seq[UmlClassMember] = ex.solution.classes flatMap members distinct

}


// Table classes

case class UmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, solution: UmlClassDiagram, markedText: String, toIgnore: String)
  extends Exercise {

  def splitToIgnore: Seq[String] = toIgnore split tagJoinChar

}

// FIXME: save ignore words and mappings as json!?!
case class UmlMapping(exerciseId: Int, key: String, value: String)

case class UmlSolution(username: String, exerciseId: Int, part: UmlExPart, classDiagram: UmlClassDiagram) extends PartSolution {

  override type PartType = UmlExPart

}

// Tables

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[UmlExercise, UmlCompleteEx, UmlSolution, UmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = UmlExercisesTable

  override protected type SolTableDef = UmlSolutionsTable

  // Table Queries

  override protected val exTable  = TableQuery[UmlExercisesTable]
  override protected val solTable = TableQuery[UmlSolutionsTable]

  private val umlMappings = TableQuery[UmlMappingsTable]

  // Reading

  override def completeExForEx(ex: UmlExercise): Future[UmlCompleteEx] = for {
    mappings <- db.run(umlMappings filter (_.exerciseId === ex.id) result)
  } yield UmlCompleteEx(ex, mappings)


  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx): Future[Boolean] =
    saveSeq[UmlMapping](compEx.mappings, m => db.run(umlMappings += m))

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.urlName, str => UmlExParts.values.find(_.urlName == str) getOrElse ClassSelection)

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def solutionAsJson = column[UmlClassDiagram]("solution_json")

    def markedText = column[String]("marked_text")

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, solutionAsJson, markedText, toIgnore) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, mappingKey))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable[UmlSolution](tag, "uml_solutions") {

    def solutionJson = column[UmlClassDiagram]("solution_json")


    override def pk = primaryKey("pk", (username, exerciseId, part))


    override def * = (username, exerciseId, part, solutionJson) <> (UmlSolution.tupled, UmlSolution.unapply)

  }

}