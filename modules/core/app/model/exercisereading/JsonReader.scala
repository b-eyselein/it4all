package model.exercisereading

import java.nio.file.{Files, Path, Paths}

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import io.ebean.Finder
import model.JsonReadable
import model.StringConsts.{EX_FILE_NAME, ID_NAME, KEY_NAME, VALUE_NAME}
import play.libs.Json

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Failure, Success, Try}

abstract class JsonReader[R <: JsonReadable](val exerciseType: String, val finder: Finder[Integer, R], classFor: Class[_]) {

  val resourcesFolder: Path = Paths.get("conf", "resources", exerciseType)

  val jsonSchema: JsonNode = new JsonSchemaGenerator(new ObjectMapper).generateJsonSchema(classFor)

  def read(node: JsonNode): SingleReadingResult[R] = {
    val id = node.get(ID_NAME).asInt
    val w = Option(finder.byId(id)).getOrElse(instantiate(id))

    update(w, node)

    SingleReadingResult(w)
  }

  val stdFile: Path = Paths.get(resourcesFolder.toString, EX_FILE_NAME)

  def update(toUpdate: R, node: JsonNode): Unit

  def save(toSave: R): Unit = toSave.saveInDB()

  def instantiate(id: Int): R

  def readFromJsonFile(path: Path = stdFile): Try[List[AbstractReadingResult]] =
    Try(new String(Files.readAllBytes(path)).replace("\t", "  ")).map { jsonAsString =>
      val jsonSchemaAsString = Json.prettyPrint(jsonSchema).replace("\t", "  ")

      val json = Json.parse(jsonAsString)

      JsonReader.validateJson(json, jsonSchema) match {
        case Failure(e) => ReadingFailure(jsonAsString, jsonSchemaAsString, e)
        case Success(report) =>
          if (report.isSuccess) ReadingResult(jsonAsString, jsonSchemaAsString, json.iterator.asScala.map(read).toList)
          else ReadingError(jsonAsString, jsonSchemaAsString, report)
      }
    }

  def checkFiles(r: R): List[Try[Path]] = List.empty

}

object JsonReader {

  def readMap(mapNode: JsonNode): Map[String, String] =
    mapNode.iterator.asScala.map(node => node.get(KEY_NAME).asText -> node.get(VALUE_NAME).asText).toMap

  def readArray(arrayNode: JsonNode): List[JsonNode] = arrayNode.iterator.asScala.toList

  def readTextArray(textArrayNode: JsonNode): List[String] = readArray(textArrayNode).map(_.asText)

  def readAndJoinTextArray(textArrN: JsonNode, joinChar: String = ""): String = readTextArray(textArrN).mkString(joinChar)

  def validateJson(json: JsonNode, jsonSchema: JsonNode): Try[ProcessingReport] =
    Try(JsonSchemaFactory.byDefault().getJsonSchema(jsonSchema).validate(json))

}