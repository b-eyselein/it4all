import {getVariablesFromGrammar, Grammar, GrammarRule} from './grammar';
import {ExtendedBackusNaurFormGrammarElement, variable, Variable} from './ebnfElements';

export type Replacers<IE> = [IE, Variable][];

interface GrammarElemConvResult<IE, OE> {
  newRules: GrammarRule<IE>[];
  updatedVariables: Variable[];
  updatedReplacers: Replacers<IE>;
}

export interface GrammarElementConversionResult<IE, OE> extends GrammarElemConvResult<IE, OE> {
  newOutputElement: OE;
}

export interface CollectionGrammarElementConversionResult<IE, OE> extends GrammarElemConvResult<IE, OE> {
  newOutputElements: OE[];
}


export abstract class GrammarConverter<IE extends ExtendedBackusNaurFormGrammarElement, IG extends Grammar<IE>, OE, OG extends Grammar<OE>> {

  protected abstract convertElement(
    element: IE,
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>,
    ruleVariable: Variable | undefined
  ): GrammarElementConversionResult<IE, OE>;

  protected abstract createOutputGrammar(startSymbol: Variable, rules: GrammarRule<OE>[]): OG;

  private goConvertGrammar(
    inputRules: GrammarRule<IE>[],
    outputRules: GrammarRule<OE>[],
    currentVariables: Variable[],
    currentReplacers: Replacers<IE>
  ): GrammarRule<OE>[] {
    if (inputRules.length === 0) {
      return outputRules;
    } else {

      const [{variable, right}, ...tail] = inputRules;

      const {newOutputElement, newRules, updatedVariables, updatedReplacers} = this.convertElement(right, currentVariables, currentReplacers, variable);

      return this.goConvertGrammar(
        [...tail, ...newRules],
        [...outputRules, {variable: variable, right: newOutputElement}],
        [...currentVariables, ...updatedVariables],
        updatedReplacers
      );
    }
  }

  convert({rules, startSymbol}: IG): OG {
    const variables: Variable[] = getVariablesFromGrammar({rules, startSymbol});

    const newRules = this.goConvertGrammar(rules, [], variables, []);

    return this.createOutputGrammar(startSymbol, newRules);
  }

  protected findNewVariables(currentVariables: Variable[]): Variable | undefined {

    const vars = Array.from(new Set(currentVariables.map(({value}) => value)));

    const foundVar = Array.from({length: 26}, (_, i) => String.fromCharCode('A'.charCodeAt(0) + i))
      .find((s) => !vars.includes(s));

    return foundVar ? variable(foundVar) : undefined;
  }

}
