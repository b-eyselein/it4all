package model.core

import better.files.File
import play.api.libs.json.{Json, Reads}

import scala.util.{Failure, Success, Try}

object ResultsFileJsonFormat {

  def readDockerExecutionResultFile[A](
    targetFile: File,
    jsonFormat: Reads[A]
  ): Try[A] =
    for {
      fileContent <- Try(targetFile.contentAsString)
      jsValue     <- Try(Json.parse(fileContent))
      result <-
        jsonFormat
          .reads(jsValue)
          .fold(
            _ => Failure(new Exception("There has been an error reading a json programing result file!")),
            result => Success(result)
          )
    } yield result

}
