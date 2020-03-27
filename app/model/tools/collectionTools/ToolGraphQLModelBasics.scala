package model.tools.collectionTools

import sangria.macros.derive.deriveObjectType
import sangria.schema.{Field, IntType, ObjectType, OutputType, StringType, fields}

trait ToolGraphQLModelBasics[ExContentType <: ExerciseContent] {

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected def sampleSolutionType[SolType](
    name: String,
    SolTypeType: OutputType[SolType]
  ): ObjectType[Unit, SampleSolution[SolType]] =
    ObjectType(
      s"${name}SampleSolution",
      fields[Unit, SampleSolution[SolType]](
        Field("id", IntType, resolve = _.value.id),
        Field("sample", SolTypeType, resolve = _.value.sample)
      )
    )

  protected val stringSampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    sampleSolutionType("String", StringType)

  val ExContentTypeType: ObjectType[Unit, ExContentType]

}
