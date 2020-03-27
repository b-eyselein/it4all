package model.tools.collectionTools.programming

import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema.{EnumType, InputObjectType, ObjectType}

object ProgrammingGraphQLModels extends ToolGraphQLModelBasics[ProgExerciseContent, ProgSolution] {

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: ObjectType[Unit, UnitTestPart] = {
    implicit val unitTestTypeType: EnumType[UnitTestType] = deriveEnumType()

    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType

    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val progSolutionType: ObjectType[Unit, ProgSolution] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    //  implicit val progTestDataType: ObjectType[Unit, ProgTestData] = deriveObjectType(
    //     ExcludeFields("input", "output")
    //  )

    deriveObjectType(ExcludeFields("testData"))
  }

  override val ExContentTypeType: ObjectType[Unit, ProgExerciseContent] = {
    //    implicit val progInputType: ObjectType[Unit, ProgInput] = deriveObjectType()
    //    implicit val progDataType: ObjectType[Unit, ProgDataType] = deriveObjectType()

    implicit val utpt: ObjectType[Unit, UnitTestPart] = unitTestPartType

    implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType

    implicit val sst: ObjectType[Unit, SampleSolution[ProgSolution]] = sampleSolutionType("Prog", progSolutionType)

    deriveObjectType(
      // TODO: include fields !?!
      ExcludeFields("inputTypes", "outputType", "baseData", "sampleTestData", "maybeClassDiagramPart")
    )
  }

  override val SolTypeInputType: InputObjectType[ProgSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = ExerciseFileInputType

    deriveInputObjectType(
      InputObjectTypeName("ProgSolutionInput"),
      ExcludeInputFields("testData")
    )
  }

}
