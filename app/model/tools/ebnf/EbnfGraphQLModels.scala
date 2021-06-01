package model.tools.ebnf

import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import sangria.schema.{EnumType, EnumValue, InputType, ObjectType, OutputType}
import sangria.macros.derive._

object EbnfGraphQLModels
    extends ToolGraphQLModelBasics[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart, EbnfResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[EbnfExercisePart] = EnumType(
    "EbnfExPart",
    values = EbnfExercisePart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, EbnfExerciseContent] = deriveObjectType()

  override val solutionInputType: InputType[EbnfGrammar] = deriveInputObjectType[EbnfGrammar](
    InputObjectTypeName("EbnfGrammarInput")
  )

  override val resultType: OutputType[EbnfResult] = deriveObjectType[Unit, EbnfResult]()

}
