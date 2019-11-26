package model.tools.collectionTools

import better.files.File
import net.jcazevedo.moultingyaml.{YamlFormat, YamlValue}
import play.api.libs.json.{Format, Json}


object FilesExerciseConsts {

  val baseResourcesPath: File = File.currentWorkingDirectory / "conf" / "resources"

}


final case class LoadExerciseFilesMessage(files: Seq[ExerciseFile], activeFileName: Option[String])


final case class ExerciseFileWorkspace(filesNum: Int, files: Seq[ExerciseFile])


final case class ExerciseFile(name: String, resourcePath: String, fileType: String, editable: Boolean, content: String)


object ExerciseFileJsonProtocol {

  val exerciseFileFormat: Format[ExerciseFile] = Json.format[ExerciseFile]

  val exerciseFileWorkspaceReads: Format[ExerciseFileWorkspace] = {
    implicit val exFileFormat: Format[ExerciseFile] = exerciseFileFormat

    Json.format[ExerciseFileWorkspace]
  }

}

object ExerciseFileYamlProtocol {

  import net.jcazevedo.moultingyaml.DefaultYamlProtocol._

  private final case class ExerciseFileWithPath(name: String, resourcePath: String, fileType: String, editable: Boolean) {

    def loadContent: ExerciseFile = {
      val completePath    = FilesExerciseConsts.baseResourcesPath / resourcePath
      val content: String = completePath.contentAsString

      ExerciseFile(name, resourcePath, fileType, editable, content)
    }

  }

  private val exerciseFileWithPathYamlFormat: YamlFormat[ExerciseFileWithPath] = yamlFormat4(ExerciseFileWithPath)

  val exerciseFileYamlFormat: YamlFormat[ExerciseFile] = new YamlFormat[ExerciseFile] {

    override def write(obj: ExerciseFile): YamlValue = {
      //      exerciseFileWithPathYamlFormat.write(obj.saveContent)
      ???
    }

    override def read(yaml: YamlValue): ExerciseFile = exerciseFileWithPathYamlFormat.read(yaml).loadContent

  }

}
