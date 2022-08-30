package model

import scala.concurrent.{ExecutionContext, Future}

final case class ExerciseCollection(
  toolId: String,
  collectionId: Int,
  title: String
)

trait CollectionRepository {
  self: play.api.db.slick.HasDatabaseConfig[slick.jdbc.JdbcProfile] =>

  import profile.api._

  protected implicit val ec: ExecutionContext

  private val collectionsTQ = TableQuery[CollectionsTable]

  def futureCollectionCountForTool(toolId: String): Future[Int] = db.run(collectionsTQ.filter(_.toolId === toolId).length.result)

  def futureCollectionsForTool(toolId: String): Future[Seq[ExerciseCollection]] = db.run(collectionsTQ.filter(_.toolId === toolId).result)

  def futureCollectionById(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] =
    db.run(collectionsTQ.filter { c => c.toolId === toolId && c.collectionId === collectionId }.result.headOption)

  def futureInsertCollection(collection: ExerciseCollection): Future[Boolean] = for {
    lineCount <- db.run(collectionsTQ += collection)
  } yield lineCount == 1

  private class CollectionsTable(tag: Tag) extends Table[ExerciseCollection](tag, "collections") {

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def title = column[String]("title")

    override def * = (toolId, collectionId, title) <> (ExerciseCollection.tupled, ExerciseCollection.unapply)

  }

}
