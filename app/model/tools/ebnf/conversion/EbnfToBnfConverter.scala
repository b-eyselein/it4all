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
      currentTerminals: Seq[Variable],
      currentReplacers: Map[IE, Variable],
      bnfElements: Seq[OE] = Seq.empty,
      newRules: Seq[IR] = Seq.empty
    ): CollectionGrammarElementConvResult[IE, OE] = ebnfElements match {
      case Nil => CollectionGrammarElementConvResult(bnfElements, currentTerminals, currentReplacers, newRules)
      case head :: tail =>
        val GrammarElemConvResult(newBnfElement, newNonTerminals, newReplacers, newlyCreatedRules) = convertElement(head, currentTerminals, currentReplacers)

        go(tail, newNonTerminals, newReplacers, bnfElements :+ newBnfElement, newRules ++ newlyCreatedRules)
    }

    go(children.toList, currentVariables, currentReplacers)
  }

  def convertElement(
    ebnfEl: ExtendedBackusNaurFormElement,
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[ExtendedBackusNaurFormElement, Variable] = Map.empty,
    ruleVariable: Option[Variable] = None
  ): GrammarElemConvResult[IE, OE] = currentReplacers.get(ebnfEl) match {
    case Some(replacer) => GrammarElemConvResult(replacer, currentVariables, currentReplacers)
    case None =>
      ebnfEl match {
        case newElement: BackusNaurFormElement => GrammarElemConvResult(newElement, currentVariables, currentReplacers)

        case EbnfOptional(element) =>
          ruleVariable match {
            case Some(_) =>
              val GrammarElemConvResult(newElement, newVariables, newReplacers, newRules) = convertElement(element)

              GrammarElemConvResult(
                EmptyWord or newElement,
                newRules = newRules,
                newVariables = currentVariables ++ newVariables,
                newReplacers = currentReplacers ++ newReplacers
              )
            case None =>
              // Replace X? with Y and new rule Y -> eps | X
              val freshNonTerminal = findNewVariable(currentVariables)

              GrammarElemConvResult(
                freshNonTerminal,
                newRules = Seq(freshNonTerminal -> (EmptyWord | element)),
                newVariables = currentVariables :+ freshNonTerminal,
                newReplacers = currentReplacers + (ebnfEl -> freshNonTerminal)
              )
          }

        case EbnfRepetitionAny(element) =>
          ruleVariable match {
            case Some(ruleVariable) =>
              val GrammarElemConvResult(newElement, newVariables, newReplacers, newRules) = convertElement(element)

              GrammarElemConvResult(
                EmptyWord or (newElement and ruleVariable),
                newRules = newRules,
                newVariables = currentVariables ++ newVariables,
                newReplacers = currentReplacers ++ newReplacers
              )
            case None =>
              // Replace X* with Y and new rule Y -> eps | (X Y)
              val nextVariable = findNewVariable(currentVariables)

              GrammarElemConvResult(
                nextVariable,
                newVariables = currentVariables :+ nextVariable,
                newReplacers = currentReplacers + (ebnfEl -> nextVariable),
                newRules = Seq(nextVariable -> (EmptyWord | (element ~ nextVariable)))
              )
          }

        case EbnfRepetitionOne(childElement) =>
          ruleVariable match {
            case Some(ruleVariable) =>
              val GrammarElemConvResult(newElement, newVariables, newReplacers, newRules) = convertElement(childElement)

              GrammarElemConvResult(
                newElement or (newElement and ruleVariable),
                newRules = newRules,
                newVariables = currentVariables ++ newVariables,
                newReplacers = currentReplacers ++ newReplacers
              )
            case None =>
              // Replace X+ with Y and new rule Y -> X X*
              val freshNonTerminal = findNewVariable(currentVariables)

              GrammarElemConvResult(
                freshNonTerminal,
                newRules = Seq(freshNonTerminal -> (childElement ~ childElement.*)),
                newVariables = currentVariables :+ freshNonTerminal,
                newReplacers = currentReplacers + (ebnfEl -> freshNonTerminal)
              )
          }

        case EbnfSequence(children) =>
          val CollectionGrammarElementConvResult(convertedChildren, newNonTerms, newReplacers, newRules) =
            convertCollectionChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(BnfSequence(convertedChildren), newNonTerms, newReplacers, newRules)

        case EbnfAlternative(children) =>
          val CollectionGrammarElementConvResult(convertedChildren, newNonTerms, newReplacers, newRules) =
            convertCollectionChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(BnfAlternative(convertedChildren), newNonTerms, newReplacers, newRules)
      }
  }

  override protected def createOutputGrammar(
    startSymbol: Variable,
    rules: Map[Variable, BackusNaurFormElement]
  ): BackusNaurFormGrammar = BackusNaurFormGrammar(startSymbol, rules)

}
