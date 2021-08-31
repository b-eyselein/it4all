package model.tools.ebnf.grammar

trait Grammar[ElType <: GrammarElement] {

  // TODO: check that there is a rule for every NonTerminal!

  val startSymbol: Variable
  val rules: Map[Variable, ElType]

  lazy val terminals: Seq[Terminal] = rules.values.flatMap(_.terminals).toSeq.distinct
  lazy val variables: Seq[Variable] = rules
    .flatMap { case (left, right) => left +: right.variables }
    .toSeq
    .distinct

  protected def stringifyRules: String = rules.toSeq
    .map { case (left, right) => " ".repeat(4) + left + " -> " + right.stringify }
    .mkString("\n")

  def stringify: String =
    s"""G = {
       |  V = {${variables.map(_.toString).mkString(", ")}},
       |  T = {${terminals.map(_.toString).mkString(", ")}}
       |  S = $startSymbol,
       |  R = {
       |$stringifyRules
       |  }
       |}""".stripMargin

//  override def toString: String = stringify

}

// Base Normalised Form

final case class BaseNormalizedFormGrammar(
  startSymbol: Variable,
  rules: Map[Variable, BaseNormalizedFormTopLevelElement]
) extends Grammar[BaseNormalizedFormTopLevelElement] {

  def alternativeForVariable(v: Variable): BaseNormalizedFormAlternative = rules(v) match {
    case a: BaseNormalizedFormAlternative => a
    case s: BaseNormalizedFormSequence    => BaseNormalizedFormAlternative(Seq(s))
    case e: BaseNormalizedFormElement     => BaseNormalizedFormAlternative(Seq(BaseNormalizedFormSequence(Seq(e))))
  }

}

// Backus-Naur-Form

final case class BackusNaurFormGrammar(
  startSymbol: Variable,
  rules: Map[Variable, BackusNaurFormElement]
) extends Grammar[BackusNaurFormElement]

// Extended Backus-Naur-Form

final case class ExtendedBackusNaurFormGrammar(
  startSymbol: Variable,
  rules: Map[Variable, ExtendedBackusNaurFormElement]
) extends Grammar[ExtendedBackusNaurFormElement]
