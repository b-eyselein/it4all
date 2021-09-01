package model.tools.ebnf.grammar

sealed trait GrammarElement {

  def stringify: String

  def terminals: Seq[Terminal]
  def variables: Seq[Variable]

}

final case class EarleyProductionSequence(children: Seq[EarleyProductionSequenceContent]) extends GrammarElement with CollectionElement {

  override val operator: String = ", "

}

sealed trait EarleyProductionSequenceContent extends GrammarElement

// Collection: Alternative, Sequence

trait CollectionElement extends GrammarElement {

  val children: Seq[GrammarElement]
  val operator: String

  override def stringify: String = children
    .map {
      case ce: CollectionElement => "(" + ce.stringify + ")"
      case other                 => other.stringify
    }
    .mkString(operator)

  override def terminals: Seq[Terminal] = children.flatMap(_.terminals)
  override def variables: Seq[Variable] = children.flatMap(_.variables)

}

// Base Normalized Form

sealed trait BaseNormalizedFormElement extends BaseNormalizedFormTopLevelElement {

  val value: String

  def +(that: BaseNormalizedFormElement): BaseNormalizedFormSequence = BaseNormalizedFormSequence(Seq(this, that))

  override def toString: String = value

}

sealed trait BaseNormalizedFormTopLevelElement extends GrammarElement {

  def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean

}

final case class BaseNormalizedFormSequence(children: Seq[BaseNormalizedFormElement]) extends BaseNormalizedFormTopLevelElement with CollectionElement {

  def +(that: BaseNormalizedFormElement): BaseNormalizedFormSequence = BaseNormalizedFormSequence(children :+ that)

  override def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean =
    children.forall(_.producesEmptyWord(grammar, seenVars))

  override val operator: String = ", "
}

final case class BaseNormalizedFormAlternative(children: Seq[BaseNormalizedFormSequence]) extends BaseNormalizedFormTopLevelElement with CollectionElement {

  override val operator: String = " | "

  override def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean =
    children.exists(_.producesEmptyWord(grammar, seenVars))

}

// Backus-Naur-Form

sealed trait BackusNaurFormElement extends GrammarElement {

  def or(that: BackusNaurFormElement): BnfAlternative = this match {
    case BnfAlternative(children) => BnfAlternative(children :+ that)
    case _                        => BnfAlternative(Seq(this, that))
  }

  def and(that: BackusNaurFormElement): BnfSequence = this match {
    case BnfSequence(children) => BnfSequence(children :+ that)
    case _                     => BnfSequence(Seq(this, that))
  }

}

final case class BnfSequence(children: Seq[BackusNaurFormElement]) extends BackusNaurFormElement with CollectionElement {

  override val operator = ", "

}

final case class BnfAlternative(children: Seq[BackusNaurFormElement]) extends BackusNaurFormElement with CollectionElement {

  override val operator = " | "

}

// Extended Backus-Naur-Form

sealed trait ExtendedBackusNaurFormElement extends GrammarElement {

  def ? : EbnfOptional = EbnfOptional(this)

  def * : EbnfRepetitionAny = EbnfRepetitionAny(this)

  def + : EbnfRepetitionOne = EbnfRepetitionOne(this)

  def |(that: ExtendedBackusNaurFormElement): EbnfAlternative = this match {
    case EbnfAlternative(children) => EbnfAlternative(children :+ that)
    case _                         => EbnfAlternative(Seq(this, that))
  }

  def ~(that: ExtendedBackusNaurFormElement): EbnfSequence = this match {
    case EbnfSequence(children) => EbnfSequence(children :+ that)
    case _                      => EbnfSequence(Seq(this, that))
  }

}

final case class EbnfSequence(children: Seq[ExtendedBackusNaurFormElement]) extends ExtendedBackusNaurFormElement with CollectionElement {
  override val operator = " , "

}

final case class EbnfAlternative(children: Seq[ExtendedBackusNaurFormElement]) extends ExtendedBackusNaurFormElement with CollectionElement {

  override val operator = " | "

}

// Unary: Optional, RepetitionOne, RepetitionAny

sealed trait EbnfUnaryOperatorElement extends ExtendedBackusNaurFormElement {

  val childElement: ExtendedBackusNaurFormElement

  val operator: String

  override def stringify: String = childElement.stringify + operator

  override def terminals: Seq[Terminal] = childElement.terminals
  override def variables: Seq[Variable] = childElement.variables

}

final case class EbnfOptional(childElement: ExtendedBackusNaurFormElement) extends ExtendedBackusNaurFormElement with EbnfUnaryOperatorElement {

  override val operator: String = "?"

}

final case class EbnfRepetitionAny(childElement: ExtendedBackusNaurFormElement) extends ExtendedBackusNaurFormElement with EbnfUnaryOperatorElement {

  override val operator: String = "*"

}

final case class EbnfRepetitionOne(childElement: ExtendedBackusNaurFormElement) extends ExtendedBackusNaurFormElement with EbnfUnaryOperatorElement {

  override val operator: String = "+"

}

// Common elements: Variable, Terminal, EmptyWord

final case class Variable(value: String, index: Option[Int] = None)
    extends BaseNormalizedFormElement
    with BackusNaurFormElement
    with ExtendedBackusNaurFormElement
    with EarleyProductionSequenceContent {

  override def stringify: String = value + index.map(_.toString).getOrElse("")

  override def terminals: Seq[Terminal] = Seq.empty
  override def variables: Seq[Variable] = Seq(this)

  override def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean =
    !seenVars.contains(this) || grammar.rules(this).producesEmptyWord(grammar, seenVars :+ this)

}

final case class Terminal(value: String)
    extends BaseNormalizedFormElement
    with BackusNaurFormElement
    with ExtendedBackusNaurFormElement
    with EarleyProductionSequenceContent {

  override def stringify: String = s"'$value'"

  override def terminals: Seq[Terminal] = Seq(this)
  override def variables: Seq[Variable] = Seq.empty

  override def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean = false

}

case object EmptyWord extends BaseNormalizedFormElement with BackusNaurFormElement with ExtendedBackusNaurFormElement {

  override def stringify = "ε"

  override def terminals: Seq[Terminal] = Seq.empty
  override def variables: Seq[Variable] = Seq.empty

  override def producesEmptyWord(grammar: BaseNormalizedFormGrammar, seenVars: Seq[Variable]): Boolean = true

  override val value: String = "ε"

}
