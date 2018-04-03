package model.uml

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.uml.UmlConsts._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping])
  extends PartsCompleteEx[UmlExercise, UmlExPart] {

  override def preview: Html = views.html.uml.umlPreview(this)

  private val solution: UmlClassDiagram = ex.solution

  def getClassesForDiagDrawingHelp: String = {
    val solution = ex.solution
    val sqrt = Math.round(Math.sqrt(solution.classes.size))

    solution.classes.zipWithIndex.map { case (clazz, index) =>
      s"""{
         |  name: "${clazz.className}",
         |  classType: "${clazz.classType.toString.toUpperCase}", attributes: [], methods: [],
         |  position: { x: ${(index / sqrt) * GapHorizontal + OFFSET}, y: ${(index % sqrt) * GapVertival + OFFSET} }
         |}""".stripMargin
    } mkString ","
  }

  override def hasPart(partType: UmlExPart): Boolean = partType match {
    case (ClassSelection) => true
    case DiagramDrawing   => false // TODO: Currently deactivated...
    case _                => false
  }

  def allAttributes: Seq[UmlClassDiagClassAttribute] = solution.classes flatMap (_.attributes) groupBy (attr => (attr.name, attr.memberType)) map (_._2.head) toSeq

  def allMethods: Seq[UmlClassDiagClassMethod] = solution.classes flatMap (_.methods) groupBy (method => (method.name, method.memberType)) map (_._2.head) toSeq

}


// Table classes

case class UmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, solution: UmlClassDiagram, classSelText: String, diagDrawText: String, toIgnore: String)
  extends Exercise {

  def splitToIgnore: Seq[String] = toIgnore split tagJoinChar

}

case class UmlMapping(exerciseId: Int, key: String, value: String)

case class UmlSolution(username: String, exerciseId: Int, part: UmlExPart, classDiagram: UmlClassDiagram) extends PartSolution {

  override type PartType = UmlExPart

}

// Tables

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[UmlExercise, UmlCompleteEx, UmlSolution, UmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = UmlExercisesTable

  override protected type SolTableDef = UmlSolutionsTable

  // Table Queries

  override protected val exTable = TableQuery[UmlExercisesTable]

  override protected val solTable = TableQuery[UmlSolutionsTable]

  val umlMappings = TableQuery[UmlMappingsTable]

  // Reading

  override def completeExForEx(ex: UmlExercise)(implicit ec: ExecutionContext): Future[UmlCompleteEx] = for {
    mappings <- db.run(umlMappings filter (_.exerciseId === ex.id) result)
  } yield UmlCompleteEx(ex, mappings)


  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] =
    saveSeq[UmlMapping](compEx.mappings, m => db.run(umlMappings += m))

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.urlName, str => UmlExParts.values.find(_.urlName == str) getOrElse ClassSelection)

  protected implicit val ucdColumnType: BaseColumnType[UmlClassDiagram] = {

    val write = (ucd: UmlClassDiagram) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(ucd).toString

    val read = (str: String) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
      case JsSuccess(ucd, _) => ucd
      case JsError(errors)   =>
        errors.foreach(error => Logger.error("There has been an error loading a uml class diagram from json" + error))
        UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty)
    }

    MappedColumnType.base[UmlClassDiagram, String](write, read)
  }

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def solutionAsJson = column[UmlClassDiagram]("solution_json")

    def classSelText = column[String]("class_sel_text")

    def diagDrawText = column[String]("diag_draw_text")

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, solutionAsJson, classSelText, diagDrawText, toIgnore) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, mappingKey))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable[UmlSolution](tag, "uml_solutions") {

    def solutionJson = column[UmlClassDiagram]("solution_json")


    override def pk = primaryKey("pk", (username, exerciseId, part))


    override def * = (username, exerciseId, part, solutionJson) <> (UmlSolution.tupled, UmlSolution.unapply)

  }

}