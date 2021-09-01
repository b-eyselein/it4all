package model.tools.ebnf.conversion

import model.tools.ebnf.grammar.{Grammar, GrammarElement, Variable}

final case class GrammarElemConvResult[IE <: GrammarElement, OE <: GrammarElement](
  newOutputElement: OE,
  newVariables: Seq[Variable] = Seq.empty,
  newReplacers: Map[IE, Variable] = Map.empty[IE, Variable],
  newRules: Seq[(Variable, IE)] = Seq.empty
)

final case class CollectionGrammarElementConvResult[IE <: GrammarElement, OE <: GrammarElement](
  newOutputElements: Seq[OE],
  newVariables: Seq[Variable] = Seq.empty,
  newReplacers: Map[IE, Variable] = Map.empty[IE, Variable],
  newRules: Seq[(Variable, IE)] = Seq.empty
)

trait GrammarConverter {

  protected type IE <: GrammarElement
  protected type IG <: Grammar[IE]
  protected type IR = (Variable, IE)

  protected type OE <: GrammarElement
  protected type OG <: Grammar[OE]
  protected type OR = (Variable, OE)

  def convertElement(
    element: IE,
    currentVariable: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty,
    ruleVariable: Option[Variable] = None
  ): GrammarElemConvResult[IE, OE]

  def convert(grammar: IG): OG = {

    @annotation.tailrec
    def go(
      inputRules: List[IR],
      outputRules: Seq[OR],
      currentNonTerminals: Seq[Variable],
      currentReplacers: Map[IE, Variable]
    ): OG = inputRules match {
      case Nil => createOutputGrammar(grammar.startSymbol, outputRules.toMap)
      case (left, right) :: tail =>
        val GrammarElemConvResult(newRight, newNonTerminals, newReplacers, newRules) =
          convertElement(right, currentNonTerminals, currentReplacers, Some(left))

        go(
          tail ++ newRules,
          outputRules :+ (left -> newRight),
          currentNonTerminals ++ newNonTerminals,
          currentReplacers ++ newReplacers
        )
    }

    go(grammar.rules.toList, Seq.empty, grammar.variables, Map.empty)
  }

  protected def createOutputGrammar(startSymbol: Variable, rules: Map[Variable, OE]): OG

  def findNewVariable(currentTerminals: Seq[Variable]): Variable = {
    val countedSymbols = for {
      maybeIndex <- Seq(None) ++ (0 to 9).map(Some.apply)
      letter     <- 'A' to 'Z'
    } yield Variable(letter.toString, maybeIndex)

    countedSymbols.find(newNonTerm => !currentTerminals.contains(newNonTerm)).get
  }

}
