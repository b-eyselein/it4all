package model.learningPath

import model.core.CoreConsts._
import model.learningPath.LearningPathSectionType.{QuestionSectionType, TextSectionType}
import model.persistence.TableDefs
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{Format, JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait LearningPathTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table queries


  protected val learningPaths = TableQuery[LearningPathsTable]

  protected val learningPathSections = TableQuery[LearningPathSectionsTable]

  // Queries

  def futureLearningPaths(toolUrl: String): Future[Seq[LearningPath]] = for {
    pathBases <- db.run(learningPaths.filter(_.toolUrl === toolUrl).result)
    paths <- Future.sequence(pathBases map { case (_, id, title) =>
      futureSectionsForPath(toolUrl, id) map (sections => LearningPath(toolUrl, id, title, sections))
    })
  } yield paths

  def futureLearningPathById(toolUrl: String, pathId: Int): Future[Option[LearningPath]] = for {
    maybeLpBase <- db.run(learningPaths.filter(lp => lp.toolUrl === toolUrl && lp.id === pathId).result.headOption)
    lpSections <- futureSectionsForPath(toolUrl, pathId)
  } yield maybeLpBase map (lpBase => LearningPath(lpBase._1, lpBase._2, lpBase._3, lpSections))

  private def futureSectionsForPath(toolUrl: String, pathId: Int): Future[Seq[LearningPathSection]] =
    db.run(learningPathSections.filter(lps => lps.toolUrl === toolUrl && lps.pathId === pathId).result)

  def futureSaveLearningPaths(learningPathsToSave: Seq[LearningPath]): Future[Boolean] = Future.sequence(learningPathsToSave map { lp =>
    for {
      // Delete old entry first
      _ <- db.run(learningPaths.filter(_.id === lp.id).delete)
      _ <- db.run(learningPaths += (lp.toolUrl, lp.id, lp.title))
      sectionsSaved <- saveSeq[LearningPathSection](lp.sections, lps => db.run(learningPathSections += lps))
    } yield sectionsSaved
  }) map (_ forall identity)

  // Learning paths

  class LearningPathsTable(tag: Tag) extends Table[(String, Int, String)](tag, "learning_paths") {

    def toolUrl = column[String]("tool_url")

    def id = column[Int](idName)

    def title = column[String](titleName)


    def pk = primaryKey("pk", (toolUrl, id))


    override def * = (toolUrl, id, title)

  }

  class LearningPathSectionsTable(tag: Tag) extends Table[LearningPathSection](tag, "learning_path_sections") {

    def toolUrl = column[String]("tool_url")

    def id = column[Int](idName)

    def pathId = column[Int]("path_id")

    def sectionType = column[LearningPathSectionType]("section_type")

    def title = column[String](titleName)

    def content = column[String](contentName)


    def pk = primaryKey("pk", (id, toolUrl, pathId))

    def pathFk = foreignKey("path_fk", (toolUrl, pathId), learningPaths)(p => (p.toolUrl, p.id))


    override def * = (id, toolUrl, pathId, title, content, sectionType) <> (tupled, unapplied)


    private implicit val lPQuestionJsonFormat: Format[LPQuestion] = LPQuestionJsonFormat.lpQuestionJsonFormat

    private def tupled(values: (Int, String, Int, String, String, LearningPathSectionType)): LearningPathSection = values._6 match {
      case TextSectionType     => TextSection(values._1, values._2, values._3, values._4, values._5)
      case QuestionSectionType => QuestionSection(values._1, values._2, values._3, values._4, readQuestionsFromJson(values._5))
    }

    private def readQuestionsFromJson(string: String): Seq[LPQuestion] = Json.fromJson[Seq[LPQuestion]](Json.parse(string)) match {
      case JsSuccess(res, _) => res
      case JsError(errors)   =>
        errors.foreach(error => Logger.error(error.toString))
        Seq.empty
    }

    private def unapplied(lps: LearningPathSection): Option[(Int, String, Int, String, String, LearningPathSectionType)] = lps match {
      case TextSection(id, toolUrl, pathId, title, text)          => Some((id, toolUrl, pathId, title, text, TextSectionType))
      case QuestionSection(id, toolUrl, pathId, title, questions) => Some((id, toolUrl, pathId, title, Json.toJson(questions).toString, QuestionSectionType))
    }

  }

}
