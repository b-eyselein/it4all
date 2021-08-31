package model.tools.ebnf

import model.{ExPart, ExParts, ExerciseContent}

sealed abstract class EbnfExercisePart(val partName: String, val id: String) extends ExPart

object EbnfExercisePart extends ExParts[EbnfExercisePart] {

  val values: IndexedSeq[EbnfExercisePart] = findValues

  case object GrammarCreation extends EbnfExercisePart("Erstellung einer Grammatik", "grammarCreation")

}

final case class EbnfExerciseContent(
  sampleSolutions: Seq[EbnfGrammar]
) extends ExerciseContent {

  override protected type S = EbnfGrammar

  override def parts: Seq[ExPart] = Seq(EbnfExercisePart.GrammarCreation)

}

final case class EbnfRule(variable: String, rule: String)

final case class EbnfGrammar(
  startSymbol: String,
  rules: Seq[EbnfRule]
)
