package model

import better.files.File
import model.tools.{ExTag, Exercise, ExerciseCollection, SemanticVersion}
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.output.{OutputOptions, WriteTSToFiles}
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue

object TsInterfaceGenerator extends DefaultTSTypes {

  private val clientBaseDir: File = File.currentWorkingDirectory / "ui" / "src" / "app"

  private val exerciseTSI: TSIType[Exercise] = {
    implicit val svt: TSIType[SemanticVersion] = TSType.fromCaseClass
    implicit val ett: TSIType[ExTag]           = TSType.fromCaseClass
    implicit val jvtt: TSType[JsValue]         = TSType.sameAs[JsValue, Any](anyTSType)

    TSType.fromCaseClass
  }

  private val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]

  private final case class TsTypesFile(
    tsTypes: Seq[TypescriptNamedType],
    targetFile: File
  )

  def main(args: Array[String]): Unit = {

    val targetFile = clientBaseDir / "_interfaces" / "models.ts"

    val toOutput = Seq(
      exerciseTSI.get,
      exerciseCollectionTSI.get
    )

    val options = OutputOptions(targetFile.toJava, withSemicolon = true)

    WriteTSToFiles.write(options)(toOutput)
  }

}
