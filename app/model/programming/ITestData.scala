package model.programming

import model.Enums.ExerciseState
import model.{TableDefs, User}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

object ITestData {
  val VALUES_SPLIT_CHAR = "#"
}

abstract sealed class ITestData(val id: Int, val exerciseId: Int, val inputs: String, val output: String) {
  def getInput: List[String] = inputs.split(ITestData.VALUES_SPLIT_CHAR).toList
}

case class SampleTestData(i: Int, e: Int, is: String, o: String) extends ITestData(i, e, is, o)

case class CommitedTestData(i: Int, e: Int, userName: String, is: String, o: String, state: ExerciseState) extends ITestData(i, e, is, o)

object CommitedTestDataHelper {

  def forUserAndExercise(user: User, exerciseId: Int): List[CommitedTestData] = {
    //      finder.all().stream().filter(td => td.user.name.equals(user.name) && td.exerciseId == exerciseId)
    List.empty
  }

}

private[model] trait TestData extends ProgExercises with TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  abstract class ITestDataTable[T <: ITestData](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("ID", O.AutoInc)

    def exerciseId = column[Int]("EXERCISE_ID")

    def inputs = column[String]("INPUTS")

    def output = column[String]("OUTPUT")


    def pk = primaryKey("PK", (id, exerciseId))

    def exerciseFk = foreignKey("EXERCISE_FK", exerciseId, progExercises)(_.id)

  }

  class SampleTestDataTable(tag: Tag) extends ITestDataTable[SampleTestData](tag, "SAMPLE_TEST_DATA") {

    def * = (id, exerciseId, inputs, output) <> (SampleTestData.tupled, SampleTestData.unapply)

  }

  lazy val sampleTestData = TableQuery[SampleTestDataTable]

  implicit val ApprovalStateColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.name, str => Option(ExerciseState.valueOf(str)).getOrElse(ExerciseState.CREATED))

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "COMMITED_TEST_DATE") {

    def userName = column[String]("USER_NAME")

    def state = column[ExerciseState]("APPROVAL_STATE")


    def userFk = foreignKey("USER_FK", userName, users)(_.username)


    def * = (id, exerciseId, userName, inputs, output, state) <> (CommitedTestData.tupled, CommitedTestData.unapply)

  }

  lazy val commitedTestData = TableQuery[CommitedTestDataTable]

}
