package model.tools.ebnf

import enumeratum.PlayEnum
import model.{ExPart, ExerciseContent}

@deprecated()
sealed abstract class EbnfExercisePart(val partName: String, val id: String) extends ExPart

object EbnfExercisePart extends PlayEnum[EbnfExercisePart] {

  val values: IndexedSeq[EbnfExercisePart] = findValues

  case object GrammarCreation extends EbnfExercisePart("Erstellung einer Grammatik", "grammarCreation")

}

final case class EbnfExerciseContent(
  predefinedTerminals: Option[Seq[String]],
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
