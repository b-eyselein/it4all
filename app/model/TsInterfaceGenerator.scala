package model

import better.files.File
import model.tools.collectionTools.BasicTSTypes
import model.tools.collectionTools.sql.SqlTSTypes
import nl.codestar.scalatsi.DefaultTSTypes
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.output.{OutputOptions, WriteTSToFiles}

object TsInterfaceGenerator extends DefaultTSTypes {

  private final case class TsTypesFile(
    tsTypes: Seq[TypescriptNamedType],
    targetFile: File
  )

  private val clientBaseDir: File = File.currentWorkingDirectory / "ui" / "src" / "app"

  private def collToolInterfacesFile(toolId: String): File =
    clientBaseDir / "tools" / "collection-tools" / toolId / s"$toolId-interfaces.ts"

  private val tsTypesFiles = Seq(
    TsTypesFile(BasicTSTypes.exported, clientBaseDir / "_interfaces" / "models.ts"),
    TsTypesFile(SqlTSTypes.exported, collToolInterfacesFile("sql"))
  )

  def main(args: Array[String]): Unit = tsTypesFiles.foreach {
    case TsTypesFile(toOutput, targetFile) =>
      val options = OutputOptions(targetFile.toJava, withSemicolon = true)

      WriteTSToFiles.write(options)(toOutput)
  }

}
