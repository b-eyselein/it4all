package model

import scala.concurrent.Future

final case class ExerciseCollection(
  toolId: String,
  collectionId: Int,
  title: String
)

trait CollectionRepository {
  self: TableDefs =>

  import profile.api._

  protected val collectionsTQ = TableQuery[CollectionsTable]

  def futureCollectionCountForTool(toolId: String): Future[Int] = db.run(collectionsTQ.filter { _.toolId === toolId }.length.result)

  def futureCollectionsForTool(toolId: String): Future[Seq[ExerciseCollection]] =
    db.run(collectionsTQ.filter { _.toolId === toolId }.sortBy { _.collectionId }.result)

  def futureCollectionById(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] = db.run(
    collectionsTQ
      .filter { c => c.toolId === toolId && c.collectionId === collectionId }
      .result
      .headOption
  )

  def futureInsertCollection(collection: ExerciseCollection): Future[Unit] = for {
    _ <- db.run(collectionsTQ += collection)
  } yield ()

  protected class CollectionsTable(tag: Tag) extends Table[ExerciseCollection](tag, "collections") {
    def toolId       = column[String]("tool_id")
    def collectionId = column[Int]("collection_id")
    def title        = column[String]("title")

    def pk = primaryKey("collections_pk", (toolId, collectionId))

    override def * = (toolId, collectionId, title).mapTo[ExerciseCollection]
  }

}
