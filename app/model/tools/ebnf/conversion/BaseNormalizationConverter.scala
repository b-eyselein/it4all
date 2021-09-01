package model.tools.ebnf.conversion

import model.tools.ebnf.grammar._

object BaseNormalizationConverter extends GrammarConverter {

  override type IE = BackusNaurFormElement
  override type IG = BackusNaurFormGrammar

  override type OE = BaseNormalizedFormTopLevelElement
  override type OG = BaseNormalizedFormGrammar

  def convertAlternativeChildElements(
    elems: Seq[IE],
    currentVariables: Seq[Variable],
    currentReplacers: Map[IE, Variable]
  ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormSequence] = {

    @annotation.tailrec
    def go(
      remainingElems: List[IE],
      curVariables: Seq[Variable],
      curReplacers: Map[IE, Variable],
      acc: Seq[BaseNormalizedFormSequence] = Seq.empty,
      newRules: Seq[(Variable, IE)] = Seq.empty
    ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormSequence] = remainingElems match {
      case Nil => CollectionGrammarElementConvResult(acc, curVariables, curReplacers, newRules)
      case head :: tail =>
        head match {
          case x: BaseNormalizedFormElement => go(tail, curVariables, curReplacers, acc :+ BaseNormalizedFormSequence(Seq(x)), newRules)
          case BnfAlternative(children)     => go(children.toList ++ tail, curVariables, curReplacers, acc, newRules)
          case BnfSequence(children) =>
            val CollectionGrammarElementConvResult(newOutputElements, newVariables, newReplacers, newRules) =
              convertSequenceChildElements(children, curVariables, curReplacers)

            go(tail, newVariables, newReplacers, acc :+ BaseNormalizedFormSequence(newOutputElements), newRules)
        }
    }

    go(elems.toList, currentVariables, currentReplacers)
  }

  def convertSequenceChildElements(
    elems: Seq[IE],
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty
  ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormElement] = {

    @annotation.tailrec
    def go(
      remainingElems: List[IE],
      curVariables: Seq[Variable],
      curReplacers: Map[IE, Variable],
      acc: Seq[BaseNormalizedFormElement] = Seq.empty,
      newRules: Seq[(Variable, IE)] = Seq.empty
    ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormElement] = remainingElems match {
      case Nil => CollectionGrammarElementConvResult(acc, curVariables, curReplacers, newRules)
      case head :: tail =>
        head match {
          case x: BaseNormalizedFormElement => go(tail, curVariables, curReplacers, acc :+ x, newRules)
          case BnfSequence(children)        => go(children.toList ++ tail, curVariables, curReplacers, acc, newRules)
          case a: BnfAlternative =>
            val nextVariable = findNewVariable(curVariables)

            go(tail, curVariables :+ nextVariable, curReplacers, acc :+ nextVariable, newRules :+ (nextVariable -> a))
        }
    }

    go(elems.toList, currentVariables, currentReplacers)
  }

  override def convertElement(
    bnfElement: IE,
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty,
    ruleVariable: Option[Variable] = None
  ): GrammarElemConvResult[IE, OE] = currentReplacers.get(bnfElement) match {
    case Some(replacer: Variable) => GrammarElemConvResult(replacer, currentVariables, currentReplacers)
    case None =>
      bnfElement match {
        case e: BaseNormalizedFormElement => GrammarElemConvResult(e, currentVariables, currentReplacers)

        case BnfAlternative(children) =>
          // FIXME: filter replacers with current variable?!
          val CollectionGrammarElementConvResult(
            newOutputElements,
            newVariables,
            newReplacers,
            newRules
          ) = convertAlternativeChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(
            BaseNormalizedFormAlternative(newOutputElements),
            newVariables,
            newReplacers,
            newRules
          )

        case BnfSequence(children) =>
          val CollectionGrammarElementConvResult(
            newOutputElements,
            newVariables,
            newReplacers,
            newRules
          ) = convertSequenceChildElements(children, currentVariables, currentReplacers)

          GrammarElemConvResult(
            BaseNormalizedFormSequence(newOutputElements),
            newVariables,
            newReplacers,
            newRules
          )
      }
  }

  override protected def createOutputGrammar(
    startSymbol: Variable,
    rules: Map[Variable, OE]
  ): BaseNormalizedFormGrammar = BaseNormalizedFormGrammar(startSymbol, rules)

}
