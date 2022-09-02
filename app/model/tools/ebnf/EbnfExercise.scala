package model.tools.ebnf

import model.ExerciseContent

final case class EbnfExerciseContent(
  predefinedTerminals: Option[Seq[String]],
  sampleSolutions: Seq[EbnfGrammar]
) extends ExerciseContent {

  override protected type S = EbnfGrammar

}

final case class EbnfRule(variable: String, rule: String)

final case class EbnfGrammar(
  startSymbol: String,
  rules: Seq[EbnfRule]
)
