import {CollectionGrammarElementConversionResult, GrammarConverter, GrammarElementConversionResult, Replacers} from './grammarConverter';
import {
  alternative,
  BackusNaurFormElement,
  BaseNormalizedFormGrammarElement,
  BaseNormalizedSequence,
  BasicGrammarElement,
  sequence,
  Variable
} from './ebnfElements';
import {Grammar, GrammarRule} from './grammar';
import {checkNever} from '../../../helpers';

type IE = BackusNaurFormElement;
type OE = BaseNormalizedFormGrammarElement;

class BackusNaurForm2BaseNormalizedFormConverter extends GrammarConverter<IE, Grammar<IE>, OE, Grammar<OE>> {

  protected createOutputGrammar(startSymbol: Variable, rules: GrammarRule<OE>[]): Grammar<OE> {
    return {startSymbol, rules};
  }

  private convertAlternativeChildElements(
    remainingElements: IE[],
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>,
    acc: BaseNormalizedSequence[] = [],
    newRules: GrammarRule<IE>[] = []
  ): CollectionGrammarElementConversionResult<IE, BaseNormalizedSequence> {
    if (remainingElements.length === 0) {
      return {newOutputElements: acc, newRules, updatedVariables: currentVariables, updatedReplacers: currentReplacers};
    }

    const [head, ...tail] = remainingElements;

    if (head._type === 'EmptyWord' || head._type === 'Terminal' || head._type === 'Variable') {
      return this.convertAlternativeChildElements(tail, currentVariables, currentReplacers, [...acc, sequence(head)], newRules);
    }

    if (head._type === 'Alternative') {
      return this.convertAlternativeChildElements([...head.children, ...tail], currentVariables, currentReplacers, acc, newRules);
    }

    if (head._type === 'Sequence') {
      const {newOutputElements, newRules, updatedVariables, updatedReplacers} =
        this.convertSequenceChildElements(head.children, currentVariables, currentReplacers);

      return this.convertAlternativeChildElements(
        tail,
        updatedVariables,
        updatedReplacers,
        [...acc, sequence<BasicGrammarElement>(...newOutputElements)],
        newRules
      );
    }

    return checkNever(head, {newOutputElements: head, newRules, updatedVariables: currentVariables, updatedReplacers: currentReplacers});
  }

  private convertSequenceChildElements(
    remainingElements: IE[],
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>,
    acc: BasicGrammarElement[] = [],
    newRules: GrammarRule<IE>[] = []
  ): CollectionGrammarElementConversionResult<IE, BasicGrammarElement> {
    if (remainingElements.length === 0) {
      return {newOutputElements: acc, newRules, updatedVariables: currentVariables, updatedReplacers: currentReplacers};
    }

    const [head, ...tail] = remainingElements;

    if (head._type === 'EmptyWord' || head._type === 'Terminal' || head._type === 'Variable') {
      return this.convertSequenceChildElements(tail, currentVariables, currentReplacers, [...acc, head], newRules);
    }

    if (head._type === 'Sequence') {
      return this.convertSequenceChildElements([...head.children, ...tail], currentVariables, currentReplacers, acc, newRules);
    }

    if (head._type === 'Alternative') {
      const variable = this.findNewVariables(currentVariables)!;

      return this.convertSequenceChildElements(
        tail,

        [...currentVariables, variable],
        currentReplacers,
        [...acc, variable],
        [...newRules, {variable, right: head}]);
    }

    return checkNever(head, {newOutputElements: head, newRules, updatedVariables: currentVariables, updatedReplacers: currentReplacers});
  }

  protected convertElement(element: IE, currentVariables: Variable[], currentReplacers: Replacers<IE>, ruleVariable: Variable | undefined): GrammarElementConversionResult<IE, OE> {

    if (element._type === 'EmptyWord' || element._type === 'Variable' || element._type === 'Terminal') {
      return {newOutputElement: alternative(sequence(element)), newRules: [], updatedVariables: currentVariables, updatedReplacers: currentReplacers};
    }

    if (currentReplacers.length > 0) {
      const foundReplacer = currentReplacers.find((el) => JSON.stringify(el) === JSON.stringify(element));

      if (foundReplacer) {
        return {
          newOutputElement: alternative(sequence(foundReplacer[1])),
          newRules: [],
          updatedVariables: currentVariables,
          updatedReplacers: currentReplacers
        };
      }
    }

    if (element._type === 'Alternative') {

      const {newOutputElements, newRules, updatedVariables, updatedReplacers} =
        this.convertAlternativeChildElements(element.children, currentVariables, currentReplacers);

      return {newOutputElement: alternative(...newOutputElements), newRules, updatedVariables, updatedReplacers};
    }

    if (element._type === 'Sequence') {
      const {newOutputElements, newRules, updatedVariables, updatedReplacers} =
        this.convertSequenceChildElements(element.children, currentVariables, currentReplacers);

      return {newOutputElement: alternative(sequence(...newOutputElements)), newRules, updatedVariables, updatedReplacers};
    }

    return checkNever(element, {newOutputElement: element, newRules: [], updatedVariables: currentVariables, updatedReplacers: currentReplacers});
  }

}

export const backusNaurForm2BaseNormalizedFormConverter = new BackusNaurForm2BaseNormalizedFormConverter();
