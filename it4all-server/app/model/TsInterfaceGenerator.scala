package model


import better.files.File
import nl.codestar.scalatsi.DefaultTSTypes
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.output.{OutputOptions, WriteTSToFiles}


object TsInterfaceGenerator extends DefaultTSTypes {

  private final case class TsTypesFile(
    tsTypes: Seq[TypescriptNamedType],
    targetFile: File
  )

  private val clientBaseDir: File = File.currentWorkingDirectory.parent / "it4all-client" / "src" / "app"

  private def collToolInterfacesFile(toolId: String): File =
    clientBaseDir / "tools" / "collection-tools" / toolId / s"${toolId}_interfaces.ts"

  private val tsTypesFiles = Seq(
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.exerciseCollectionTSI.get,
        MyTSInterfaceTypes.exerciseMetaDataTSI.get,
        MyTSInterfaceTypes.exerciseTSI.get,

        /*
        MyTSInterfaceTypes.lessonTextContentTSI.get,
        MyTSInterfaceTypes.lessonQuestionsContentTSI.get,
        MyTSInterfaceTypes.lessonTSI.get,
         */
      ),
      clientBaseDir / "_interfaces" / "models.ts"
    ),

    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.regexExerciseContentTSI.get,
      ),
      collToolInterfacesFile("regex")
    ),
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.progExerciseContentTSI.get,
        MyTSInterfaceTypes.progSolutionTSI.get,
      ),
      collToolInterfacesFile("programming")
    ),
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.sqlExerciseContentTSI.get,
        MyTSInterfaceTypes.sqlQueryResultTSI.get,
      ),
      collToolInterfacesFile("sql")
    ),
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.umlExerciseContentTSI.get,
        MyTSInterfaceTypes.umlClassDiagramTSI.get,
      ),
      collToolInterfacesFile("uml")
    ),
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.xmlExerciseContentTSI.get,
        MyTSInterfaceTypes.xmlSolutionTSI.get,
        MyTSInterfaceTypes.xmlCompleteResultTSI.get
      ),
      collToolInterfacesFile("xml")
    ),
    TsTypesFile(
      Seq(
        MyTSInterfaceTypes.webExerciseContentTSI.get,
        MyTSInterfaceTypes.webCompleteResultTSI.get
      ),
      collToolInterfacesFile("web")
    )
  )

  def main(args: Array[String]): Unit = tsTypesFiles.foreach {
    case TsTypesFile(toOutput, targetFile) =>

      val options = OutputOptions(targetFile.toJava, withSemicolon = true)

      WriteTSToFiles.write(options)(toOutput)
  }

}
