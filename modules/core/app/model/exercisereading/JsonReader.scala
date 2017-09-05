package model.exercisereading

import java.nio.file.Path
import model.WithId
import play.data.DynamicForm
import java.util.stream.Collectors
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import java.util.stream.StreamSupport
import com.fasterxml.jackson.databind.JsonNode
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import scala.util.Failure
import scala.util.Try
import scala.util.Success
import play.libs.Json
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.core.report.ProcessingReport
import java.nio.file.Files
import scala.collection.JavaConverters._
import model.StringConsts._
import io.ebean.Finder
import java.nio.file.Paths

abstract class JsonReader[W <: WithId](val exerciseType: String, val finder: Finder[Integer, W], classFor: Class[_]) {

  val jsonSchema = new JsonSchemaGenerator(new ObjectMapper).generateJsonSchema(classFor)

  def read(node: JsonNode) = {
    val id = node.get(ID_NAME).asInt
    val w = Option(finder.byId(id)).getOrElse(instantiateExercise(id));

    w
  }

  def readFromStandardFile = readFromJsonFile(Paths.get("conf", "resources", exerciseType, EX_FILE_NAME))

  def instantiateExercise(id: Int): W

  def readFromJsonFile(path: Path): AbstractReadingResult = {
    val jsonAsString = new String(Files.readAllBytes(path))
    val json = Json.parse(jsonAsString)
    val jsonSchemaAsString = Json.prettyPrint(jsonSchema)

    JsonReader.validateJson(json, jsonSchema) match {
      case Failure(e) => new ReadingFailure(jsonAsString, jsonSchemaAsString, e)
      case Success(report) =>
        if (!report.isSuccess()) new ReadingError(jsonAsString, jsonSchemaAsString, report)
        else new ReadingResult(jsonAsString, jsonSchemaAsString, json.iterator.asScala.map(read(_)).toList.asJava)
    }
  }

}

object JsonReader {

  def validateJson(json: JsonNode, jsonSchema: JsonNode): Try[ProcessingReport] = try {
    Success(JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema).validate(json))
  } catch {
    case e: Throwable => Failure(e)
  }

}