package model.tools.ebnf

import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object EbnfGraphQLModels extends ToolGraphQLModelBasics[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart, EbnfResult] with GraphQLArguments {

  override val partEnumType: EnumType[EbnfExercisePart] = EnumType(
    "EbnfExPart",
    values = EbnfExercisePart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val ebnfGrammarType: ObjectType[Unit, EbnfGrammar] = deriveObjectType(
    ReplaceField("rules", Field("rules", StringType, resolve = _ => "TODO!"))
  )

  override val exerciseContentType: ObjectType[Unit, EbnfExerciseContent] = {
    implicit val egt: ObjectType[Unit, EbnfGrammar] = ebnfGrammarType

    deriveObjectType()
  }

  private val ebnfRuleInputType: InputType[EbnfRule] = deriveInputObjectType[EbnfRule](
    InputObjectTypeName("EbnfRuleInput")
  )

  override val solutionInputType: InputType[EbnfGrammar] = {
    implicit val erit: InputType[EbnfRule] = ebnfRuleInputType

    deriveInputObjectType[EbnfGrammar](
      InputObjectTypeName("EbnfGrammarInput")
    )
  }

  override val resultType: OutputType[EbnfResult] = deriveObjectType[Unit, EbnfResult]()

}
