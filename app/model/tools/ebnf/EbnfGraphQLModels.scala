package model.tools.ebnf

import model.graphql.{GraphQLArguments, ToolWithoutPartsGraphQLModel}
import sangria.macros.derive._
import sangria.schema._

object EbnfGraphQLModels extends ToolWithoutPartsGraphQLModel[EbnfGrammar, EbnfExerciseContent, EbnfResult] with GraphQLArguments {

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
