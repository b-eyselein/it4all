package model

import java.io.FileReader
import java.nio.file.Paths
import java.sql.{Connection, SQLException}

import com.fasterxml.jackson.databind.JsonNode
import model.CommonUtils.cleanly
import model.exercise.{SqlExercise, SqlScenario}
import model.exercisereading.{ExerciseCollectionReader, ExerciseReader}
import org.apache.ibatis.jdbc.ScriptRunner
import play.Logger
import play.db.Database

import scala.collection.JavaConverters._

class SqlScenarioReader(sqlSelect: Database, sqlOther: Database)
  extends ExerciseCollectionReader[SqlExercise, SqlScenario]("sql", SqlScenario.finder, classOf[Array[SqlScenario]]) {

  val createDummy = "CREATE DATABASE IF NOT EXISTS "

  def createDatabase(databaseName: String, connection: Connection) {
    CommonUtils.cleanly(connection.createStatement)(_.close)(createStatement => {
      try {
        createStatement.executeUpdate(createDummy + databaseName)
        connection.setCatalog(databaseName)
      } catch {
        case e: SQLException => Logger.error(s"""There has been an error running an sql script: "$createDummy $databaseName"""", e)
      }
    })
  }

//  def initRemainingExFromForm(exercise: SqlScenario, form: DynamicForm) {
  //    exercise.shortName = form.get(StringConsts.SHORTNAME_NAME)
  //    exercise.scriptFile = form.get(StringConsts.SCRIPTFILE_NAME)
  //  }

  override def instantiate(id: Int) = new SqlScenario(id)

  def runCreateScript(database: Database, scenario: SqlScenario) {
    println(exerciseType)
    println(scenario.scriptFile)
    val scriptFilePath = Paths.get("conf", "resources", exerciseType, scenario.scriptFile).toAbsolutePath

    if (scriptFilePath.toFile.exists)
      Logger.error("File " + scriptFilePath + " does not exist")
    else {
      cleanly(database.getConnection)(_.close)(connection => {
        val connection = database.getConnection

        createDatabase(scenario.shortName, connection)

        val runner = new ScriptRunner(connection)
        runner.setLogWriter(null)
        runner.runScript(new FileReader(scriptFilePath.toFile))

        connection.close()
      })
    }

  }

  override def save(scenario: SqlScenario) {
    scenario.save()

    runCreateScript(sqlSelect, scenario)
    runCreateScript(sqlOther, scenario)

    // scenario.getExercises.forEach(delegateReader::save)
  }

  override def updateCollection(exercise: SqlScenario, exerciseNode: JsonNode) {
    exercise.shortName = exerciseNode.get(StringConsts.SHORTNAME_NAME).asText
    exercise.scriptFile = exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText

    exercise.exercises = ExerciseReader.readArray(exerciseNode.get(StringConsts.EXERCISES_NAME),
      SqlExerciseReader.read).map(_.read).asJava
  }

}
