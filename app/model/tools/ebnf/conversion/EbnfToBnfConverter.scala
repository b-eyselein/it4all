package model.tools.ebnf.conversion

import model.tools.ebnf.grammar._

object EbnfToBnfConverter extends GrammarConverter {

  type IE = ExtendedBackusNaurFormElement
  type IG = ExtendedBackusNaurFormGrammar

  type OE = BackusNaurFormElement
  type OG = BackusNaurFormGrammar

  def convertCollectionChildElements(
    children: Seq[IE],
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty[IE, Variable]
  ): CollectionGrammarElementConvResult[IE, OE] = {

    @annotation.tailrec
    def go(
      ebnfElements: List[IE],
      bnfElements: Seq[OE],
      newRules: Seq[IR],
      newOutputRules: Seq[OR],
      currentTerminals: Seq[Variable],
      currentReplacers: Map[IE, Variable]
    ): CollectionGrammarElementConvResult[IE, OE] = ebnfElements match {
      case Nil => CollectionGrammarElementConvResult(bnfElements, currentTerminals, currentReplacers, newRules)
      case head :: tail =>
        val GrammarElemConvResult(
          newBnfElement,
          newNonTerminals,
          newReplacers,
          newlyCreatedRules
        ) = convertElement(head, currentTerminals, currentReplacers)

        go(
          tail,
          bnfElements :+ newBnfElement,
          newRules ++ newlyCreatedRules,
          newOutputRules,
          newNonTerminals,
          newReplacers
        )
    }

    go(children.toList, Seq.empty, Seq.empty, Seq.empty, currentVariables, currentReplacers)
  }

  def convertElement(
    ebnfEl: ExtendedBackusNaurFormElement,
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[ExtendedBackusNaurFormElement, Variable] = Map.empty
  ): GrammarElemConvResult[IE, OE] = currentReplacers.get(ebnfEl) match {
    case Some(replacer) => GrammarElemConvResult(replacer, currentVariables, currentReplacers)
    case None =>
      ebnfEl match {
        case EmptyWord    => GrammarElemConvResult(EmptyWord, currentVariables, currentReplacers)
        case t: Terminal  => GrammarElemConvResult(t, currentVariables, currentReplacers)
        case nt: Variable => GrammarElemConvResult(nt, currentVariables, currentReplacers)

        case EbnfOptional(element) =>
          // Replace X? with Y and new rule Y -> eps | X
          val freshNonTerminal = findNewVariable(currentVariables)

          GrammarElemConvResult(
            freshNonTerminal,
            newRules = Seq(freshNonTerminal -> (EmptyWord | element)),
            newVariables = currentVariables :+ freshNonTerminal,
            newReplacers = currentReplacers + (ebnfEl -> freshNonTerminal)
          )

        case EbnfRepetitionAny(element) =>
          // Replace X* with Y and new rule Y -> eps | (X Y)
          val nextVariable = findNewVariable(currentVariables)

          GrammarElemConvResult(
            nextVariable,
            newVariables = currentVariables :+ nextVariable,
            newReplacers = currentReplacers + (ebnfEl -> nextVariable),
            newRules = Seq(nextVariable -> (EmptyWord | (element ~ nextVariable)))
          )

        case EbnfRepetitionOne(childElement) =>
          // Replace X+ with Y and new rule Y -> X X*
          val freshNonTerminal = findNewVariable(currentVariables)

          GrammarElemConvResult(
            freshNonTerminal,
            newRules = Seq(freshNonTerminal -> (childElement ~ childElement.*)),
            newVariables = currentVariables :+ freshNonTerminal,
            newReplacers = currentReplacers + (ebnfEl -> freshNonTerminal)
          )

        case EbnfSequence(children) =>
          val CollectionGrammarElementConvResult(convertedChildren, newNonTerms, newReplacers, newRules) =
            convertCollectionChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(BnfSequence(convertedChildren), newNonTerms, newReplacers, newRules)

        case EbnfAlternative(children) =>
          val CollectionGrammarElementConvResult(
            convertedChildren,
            newNonTerms,
            newReplacers,
            newRules
          ) = convertCollectionChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(BnfAlternative(convertedChildren), newNonTerms, newReplacers, newRules)
      }
  }

  override protected def createOutputGrammar(
    startSymbol: Variable,
    rules: Map[Variable, BackusNaurFormElement]
  ): BackusNaurFormGrammar = BackusNaurFormGrammar(startSymbol, rules)

}
