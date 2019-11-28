package model.tools.collectionTools

import better.files.File
import net.jcazevedo.moultingyaml.{YamlFormat, YamlValue}


final case class ExerciseFile(name: String, resourcePath: String, fileType: String, editable: Boolean, content: String)


object ExerciseFileYamlProtocol {

  import net.jcazevedo.moultingyaml.DefaultYamlProtocol._

  val baseResourcesPath: File = File.currentWorkingDirectory / "conf" / "resources"

  private final case class ExerciseFileWithPath(name: String, resourcePath: String, fileType: String, editable: Boolean) {

    def loadContent: ExerciseFile = {
      val completePath    = baseResourcesPath / resourcePath
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
