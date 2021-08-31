package model.tools.ebnf.conversion

import model.tools.ebnf.grammar._

object BaseNormalizationConverter extends GrammarConverter {

  override type IE = BackusNaurFormElement
  override type IG = BackusNaurFormGrammar

  override type OE = BaseNormalizedFormTopLevelElement
  override type OG = BaseNormalizedFormGrammar

  def convertAlternativeChildElements(
    elems: Seq[IE],
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty
  ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormSequence] = {

    @annotation.tailrec
    def go(
      remainingElems: List[IE],
      acc: Seq[BaseNormalizedFormSequence],
      curVariables: Seq[Variable],
      curReplacers: Map[IE, Variable],
      newRules: Seq[(Variable, IE)] = Seq.empty
    ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormSequence] = remainingElems match {
      case Nil => CollectionGrammarElementConvResult(acc, curVariables, curReplacers, newRules)
      case head :: tail =>
        head match {
          case x: BaseNormalizedFormElement => go(tail, acc :+ BaseNormalizedFormSequence(Seq(x)), curVariables, curReplacers)
          case BnfAlternative(children)     => go(children.toList ++ tail, acc, curVariables, curReplacers)
          case BnfSequence(children) =>
            val CollectionGrammarElementConvResult(newOutputElements, newVariables, newReplacers, newRules) =
              convertSequenceChildElements(children, curVariables, curReplacers)

            go(tail, acc :+ BaseNormalizedFormSequence(newOutputElements), newVariables, newReplacers, newRules)
        }
    }

    go(elems.toList, Seq.empty, currentVariables, currentReplacers)
  }

  def convertSequenceChildElements(
    elems: Seq[IE],
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty
  ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormElement] = {

    @annotation.tailrec
    def go(
      remainingElems: List[IE],
      acc: Seq[BaseNormalizedFormElement],
      curVariables: Seq[Variable],
      curReplacers: Map[IE, Variable],
      newRules: Seq[(Variable, IE)] = Seq.empty
    ): CollectionGrammarElementConvResult[IE, BaseNormalizedFormElement] = remainingElems match {
      case Nil => CollectionGrammarElementConvResult(acc, curVariables, curReplacers, newRules)
      case head :: tail =>
        head match {
          case x: BaseNormalizedFormElement => go(tail, acc :+ x, curVariables, curReplacers)
          case BnfSequence(children)        => go(children.toList ++ tail, acc, curVariables, curReplacers)
          case a: BnfAlternative =>
            val nextVariable = findNewVariable(curVariables)

            go(tail, acc :+ nextVariable, curVariables :+ nextVariable, curReplacers, newRules :+ (nextVariable -> a))
        }
    }

    go(elems.toList, Seq.empty, currentVariables, currentReplacers)
  }

  override def convertElement(
    bnfElement: IE,
    currentVariables: Seq[Variable] = Seq.empty,
    currentReplacers: Map[IE, Variable] = Map.empty
  ): GrammarElemConvResult[IE, OE] = currentReplacers.get(bnfElement) match {
    case Some(replacer: Variable) => GrammarElemConvResult(replacer, currentVariables, currentReplacers)
    case None =>
      bnfElement match {
        case v: Variable => GrammarElemConvResult(v, currentVariables, currentReplacers)
        case t: Terminal => GrammarElemConvResult(t, currentVariables, currentReplacers)
        case EmptyWord   => GrammarElemConvResult(EmptyWord, currentVariables, currentReplacers)

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
