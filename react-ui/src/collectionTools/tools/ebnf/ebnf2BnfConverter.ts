import {CollectionGrammarElementConversionResult, GrammarConverter, GrammarElementConversionResult, Replacers} from './grammarConverter';
import {alternative, BackusNaurFormElement, EmptyWord, ExtendedBackusNaurFormGrammarElement, repetitionAny, sequence, Variable} from './ebnfElements';
import {Grammar, GrammarRule} from './grammar';
import {checkNever} from '../../../helpers';

type IE = ExtendedBackusNaurFormGrammarElement;

type OE = BackusNaurFormElement;

class Ebnf2BnfConverter extends GrammarConverter<IE, Grammar<IE>, OE, Grammar<OE>> {

  protected createOutputGrammar(startSymbol: Variable, rules: GrammarRule<OE>[]): Grammar<OE> {
    return {startSymbol, rules};
  }

  convertCollectionChildElementsGo(
    ebnfElements: IE[],
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>,
    bnfElements: OE[] = [],
    currentNewRules: GrammarRule<IE>[] = [],
  ): CollectionGrammarElementConversionResult<IE, OE> {
    if (ebnfElements.length === 0) {
      return {newOutputElements: bnfElements, updatedVariables: currentVariables, updatedReplacers: currentReplacers, newRules: currentNewRules};
    }

    const [head, ...tail] = ebnfElements;

    const {newOutputElement, newRules, updatedVariables, updatedReplacers} = this.convertElement(head, currentVariables, currentReplacers);

    return this.convertCollectionChildElementsGo(
      tail,
      updatedVariables,
      updatedReplacers,
      [...bnfElements, newOutputElement],
      [...currentNewRules, ...newRules],
    );
  }

  private convertCollectionChildElements(
    children: IE[],
    currentVariables: Variable[] = [],
    currentReplacers: Replacers<IE> = []
  ): CollectionGrammarElementConversionResult<IE, OE> {
    return this.convertCollectionChildElementsGo(children, currentVariables, currentReplacers);
  }

  protected convertElement(
    element: IE,
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>,
    ruleVariable: Variable | undefined = undefined
  ): GrammarElementConversionResult<IE, OE> {

    if (element._type === 'EmptyWord' || element._type === 'Variable' || element._type === 'Terminal') {
      return {newOutputElement: element, newRules: [], updatedVariables: [], updatedReplacers: currentReplacers};
    }

    if (currentReplacers.length > 0) {
      const foundReplacer = currentReplacers
        .find(([el]) => JSON.stringify(el) === JSON.stringify(element));

      if (foundReplacer) {
        return {newOutputElement: foundReplacer[1], newRules: [], updatedReplacers: currentReplacers, updatedVariables: currentVariables};
      }
    }



    if (element._type === 'Optional') {
      if (ruleVariable) {
        const {newOutputElement, newRules, updatedVariables, updatedReplacers} = this.convertElement(element.child, currentVariables, currentReplacers);

        return {
          newOutputElement: alternative(EmptyWord, newOutputElement),
          newRules, updatedVariables, updatedReplacers
        };

      } else {
        const variable = this.findNewVariables(currentVariables)!;

        return {
          newOutputElement: variable,
          newRules: [
            {variable, right: alternative<IE>(EmptyWord, element.child)}
          ],
          updatedReplacers: [...currentReplacers, [element, variable]],
          updatedVariables: [...currentVariables, variable]
        };
      }
    }

    if (element._type === 'RepetitionAny') {

      if (ruleVariable) {
        const {
          newOutputElement: replacedChild,
          newRules,
          updatedVariables,
          updatedReplacers
        } = this.convertElement(element.child, currentVariables, currentReplacers);

        return {newOutputElement: alternative<OE>(EmptyWord, sequence(replacedChild, ruleVariable)), newRules, updatedVariables, updatedReplacers};
      } else {
        const variable = this.findNewVariables(currentVariables)!;

        return {
          newOutputElement: variable,
          newRules: [
            {variable, right: alternative<IE>(EmptyWord, sequence(element.child, variable))}
          ],
          updatedReplacers: [...currentReplacers, [element, variable]],
          updatedVariables: [...currentVariables, variable]
        };
      }
    }

    if (element._type === 'RepetitionOne') {

      if (ruleVariable) {
        const {
          newOutputElement: replacedChild,
          newRules,
          updatedVariables,
          updatedReplacers
        } = this.convertElement(element.child, currentVariables, currentReplacers);

        return {newOutputElement: alternative(replacedChild, sequence(replacedChild, ruleVariable)), newRules, updatedVariables, updatedReplacers};
      } else {
        const variable = this.findNewVariables(currentVariables)!;

        return {
          newOutputElement: variable,
          newRules: [
            {variable, right: sequence(element.child, repetitionAny(element))}
          ],
          updatedReplacers: [...currentReplacers, [element, variable]],
          updatedVariables: [...currentVariables, variable]
        };
      }
    }

    if (element._type === 'Sequence') {
      const {newOutputElements, newRules, updatedVariables, updatedReplacers} =
        this.convertCollectionChildElements(element.children, currentVariables, currentReplacers);

      return {newOutputElement: sequence(...newOutputElements), newRules, updatedReplacers, updatedVariables};
    }

    if (element._type === 'Alternative') {
      const {newOutputElements, newRules, updatedVariables, updatedReplacers} =
        this.convertCollectionChildElements(element.children, currentVariables, currentReplacers);

      return {newOutputElement: alternative(...newOutputElements), newRules, updatedReplacers, updatedVariables};
    }

    return checkNever(element, {newOutputElement: element, newRules: [], updatedVariables: currentVariables, updatedReplacers: currentReplacers});
  }

}

export const ebnf2BnfConverter = new Ebnf2BnfConverter();

