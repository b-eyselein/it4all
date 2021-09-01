import {ExtendedBackusNaurFormGrammarElement, getVariablesFromGrammarElement, Variable} from './ebnfElements';

export interface GrammarRule<ElType> {
  variable: Variable;
  right: ElType;
}


export interface Grammar<ElType> {
  startSymbol: Variable;
  rules: GrammarRule<ElType>[];
}

export function getVariablesFromGrammar({rules}: Grammar<ExtendedBackusNaurFormGrammarElement>): Variable[] {
  return Array.from(
    new Set(
      rules.flatMap(({variable, right}) => [variable, ...getVariablesFromGrammarElement(right)])
    )
  );
}
