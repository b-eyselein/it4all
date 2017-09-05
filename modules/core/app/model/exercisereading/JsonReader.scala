package model.exercisereading

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator

import io.ebean.Finder
import io.ebean.Model
import model.JsonReadable
import model.StringConsts.EX_FILE_NAME
import model.StringConsts.ID_NAME
import play.libs.Json
import java.util.stream.Collectors
import java.util.stream.StreamSupport

abstract class JsonReader[R <: JsonReadable](val exerciseType: String, val finder: Finder[Integer, R], classFor: Class[_]) {

  val jsonSchema = new JsonSchemaGenerator(new ObjectMapper).generateJsonSchema(classFor)

  def read(node: JsonNode) = {
    val id = node.get(ID_NAME).asInt
    val w = Option(finder.byId(id)).getOrElse(instantiateExercise(id));

    update(w, node)

    w
  }

  def update(toUpdate: R, node: JsonNode): Unit

  def save(toSave: R) = toSave.saveInDB()

  def readFromStandardFile = readFromJsonFile(Paths.get("conf", "resources", exerciseType, EX_FILE_NAME))

  def instantiateExercise(id: Int): R

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

  def readTextArray(textArray: JsonNode, joinChar: String) = textArray.iterator.asScala.map(_.asText).mkString(joinChar)

  def validateJson(json: JsonNode, jsonSchema: JsonNode): Try[ProcessingReport] = try {
    Success(JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema).validate(json))
  } catch {
    case e: Throwable => Failure(e)
  }

}