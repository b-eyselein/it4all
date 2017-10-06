package model

import java.io.{ FileReader, IOException }
import java.nio.file.Paths
import java.sql.{ Connection, SQLException }

import org.apache.ibatis.jdbc.ScriptRunner

import com.fasterxml.jackson.databind.JsonNode

import model.exercise.{ SqlExercise, SqlScenario }
import model.exercisereading.{ ExerciseCollectionReader, ExerciseReader }
import play.Logger
import play.data.DynamicForm
import play.db.Database

class SqlScenarioReader(sqlSelect: Database, sqlOther: Database)
    extends ExerciseCollectionReader[SqlExercise, SqlScenario]("sql", SqlScenario.finder, classOf[Array[SqlScenario]]) {

  val CREATE_DUMMY = "CREATE DATABASE IF NOT EXISTS "

  def createDatabase(databaseName: String, connection: Connection) {
    CommonUtils.cleanly(connection.createStatement)(_.close)(createStatement ⇒ {
      try {
        createStatement.executeUpdate(CREATE_DUMMY + databaseName) // NOSONAR
        connection.setCatalog(databaseName)
      } catch {
        case e: SQLException ⇒ Logger.error(s"""There has been an error running an sql script: "$CREATE_DUMMY $databaseName"""", e)
      }
    })
  }

  def initRemainingExFromForm(exercise: SqlScenario, form: DynamicForm) {
    exercise.shortName = form.get(StringConsts.SHORTNAME_NAME)
    exercise.scriptFile = form.get(StringConsts.SCRIPTFILE_NAME)
  }

  override def instantiate(id: Int) = new SqlScenario(id)

  def runCreateScript(database: Database, scenario: SqlScenario) {
    println(exerciseType)
    println(scenario.scriptFile)
    val scriptFilePath = Paths.get("conf", "resources", exerciseType, scenario.scriptFile).toAbsolutePath

    if (!scriptFilePath.toFile.exists) {
      Logger.error("File " + scriptFilePath + " does not exist")
      return
    }

    try {
      val connection = database.getConnection

      createDatabase(scenario.shortName, connection)

      val runner = new ScriptRunner(connection)
      runner.setLogWriter(null)
      runner.runScript(new FileReader(scriptFilePath.toFile))

      connection.close
    } catch {
      case error: IOException  ⇒ Logger.error("Error while executing script file " + scriptFilePath.toString, error)
      case error: SQLException ⇒ Logger.error("Error while executing script file " + scriptFilePath.toString, error)
    }

  }

  override def save(scenario: SqlScenario) {
    scenario.save

    runCreateScript(sqlSelect, scenario)
    runCreateScript(sqlOther, scenario)

    // scenario.getExercises.forEach(delegateReader::save)
  }

  override def updateCollection(exercise: SqlScenario, exerciseNode: JsonNode) {
    exercise.shortName = exerciseNode.get(StringConsts.SHORTNAME_NAME).asText
    exercise.scriptFile = exerciseNode.get(StringConsts.SCRIPTFILE_NAME).asText

    exercise.exercises = ExerciseReader.readArray(exerciseNode.get(StringConsts.EXERCISES_NAME), SqlExerciseReader.read(_))
  }

}
