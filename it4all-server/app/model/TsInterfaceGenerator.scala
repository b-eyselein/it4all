package model

import java.io.File

import nl.codestar.scalatsi.DefaultTSTypes
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.output.{OutputOptions, WriteTSToFiles}


object TsInterfaceGenerator extends DefaultTSTypes {

  val toOutput: Seq[TypescriptNamedType] = Seq(
    MyTSInterfaceTypes.exerciseCollectionTSI.get,

    MyTSInterfaceTypes.exerciseMetaDataTSI.get,

    MyTSInterfaceTypes.exerciseTSI.get,

    /*
    MyTSInterfaceTypes.lessonTextContentTSI.get,
    MyTSInterfaceTypes.lessonQuestionsContentTSI.get,
    MyTSInterfaceTypes.lessonTSI.get,
     */

    MyTSInterfaceTypes.progExerciseContentTSI.get,
    MyTSInterfaceTypes.progSolutionTSI.get,

    MyTSInterfaceTypes.regexExerciseContentTSI.get,

    MyTSInterfaceTypes.sqlExerciseContentTSI.get,
    MyTSInterfaceTypes.sqlQueryResultTSI.get,

    MyTSInterfaceTypes.umlExerciseContentTSI.get,
    MyTSInterfaceTypes.umlClassDiagramTSI.get,

    MyTSInterfaceTypes.webExerciseContentTSI.get,
    MyTSInterfaceTypes.webCompleteResultTSI.get,

    MyTSInterfaceTypes.xmlExerciseContentTSI.get
  )

  val options: OutputOptions = OutputOptions(
    targetFile = new File("/home/bjorn/workspace/it4all/it4all/it4all-client/src/app/_interfaces/models.ts"),
    withSemicolon = true
  )

  def main(args: Array[String]): Unit = WriteTSToFiles.write(options)(toOutput)

}
