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

  private val learningPaths = TableQuery[LearningPathsTable]

  private val learningPathSections = TableQuery[LearningPathSectionsTable]

  // Queries

  def futureLearningPaths: Future[Seq[LearningPath]] = Future(Seq.empty) // db.run(learningPaths.result)

  def futureLearningPathById(pathId: Int): Future[Option[LearningPath]] = for {
    maybeLpBase <- db.run(learningPaths.filter(_.id === pathId).result.headOption)
    lpSections <- futureSectionsForPath(pathId)
  } yield maybeLpBase map (lpBase => LearningPath(lpBase._1, lpBase._2, lpSections))

  private def futureSectionsForPath(pathId: Int): Future[Seq[LearningPathSection]] =
    db.run(learningPathSections.filter(_.pathId === pathId).result)

  def saveLearningPaths(learningPathsToSave: Seq[LearningPath]): Future[Boolean] = Future.sequence(learningPathsToSave map { lp =>
    for {
      // Delete old entry first
      _ <- db.run(learningPaths.filter(_.id === lp.id).delete)
      _ <- db.run(learningPaths += (lp.id, lp.title))
      sectionsSaved <- saveSeq[LearningPathSection](lp.sections, lps => db.run(learningPathSections += lps))
    } yield sectionsSaved
  }) map (_ forall identity)


  // Learning paths

  private class LearningPathsTable(tag: Tag) extends Table[(Int, String)](tag, "learning_paths") {

    def id = column[Int](idName, O.PrimaryKey)

    def title = column[String](titleName)


    override def * = (id, title)

  }

  private class LearningPathSectionsTable(tag: Tag) extends Table[LearningPathSection](tag, "learning_path_sections") {

    def id = column[Int](idName)

    def pathId = column[Int]("path_id")

    def sectionType = column[LearningPathSectionType]("section_type")

    def title = column[String](titleName)

    def content = column[String](contentName)


    def pk = primaryKey("pk", (id, pathId))

    def pathFk = foreignKey("path_fk", pathId, learningPaths)(_.id)


    override def * = (id, pathId, sectionType, title, content) <> (tupled, unapplied)

    private implicit val lPQuestionJsonFormat: Format[LPQuestion] = LPQuestionJsonFormat.lpQuestionJsonFormat

    private def tupled(values: (Int, Int, LearningPathSectionType, String, String)): LearningPathSection = values._3 match {
      case TextSectionType     => TextSection(values._1, values._2, values._4, values._5)
      case QuestionSectionType => QuestionSection(values._1, values._2, values._4, readQuestionsFromJson(values._5))
    }

    private def readQuestionsFromJson(string: String): Seq[LPQuestion] = Json.fromJson[Seq[LPQuestion]](Json.parse(string)) match {
      case JsSuccess(res, _) => res
      case JsError(errors)   =>
        errors.foreach(error => Logger.error(error.toString))
        Seq.empty
    }

    private def unapplied(lps: LearningPathSection): Option[(Int, Int, LearningPathSectionType, String, String)] = lps match {
      case TextSection(id, pathId, title, text)          => Some((id, pathId, TextSectionType, title, text))
      case QuestionSection(id, pathId, title, questions) => Some((id, pathId, QuestionSectionType, title, Json.toJson(questions).toString))
    }

  }

}
