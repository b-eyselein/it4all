import {BackusNaurFormElement, BaseNormalizedFormGrammarElement, ExtendedBackusNaurFormGrammarElement, terminal, variable, Variable} from './ebnfElements';
import {Grammar} from './grammar';

export const s = terminal('s');
export const t = terminal('t');
export const u = terminal('u');

export const termNull = terminal('0');
export const termOne = terminal('1');

export const A: Variable = variable('A');
export const B: Variable = variable('B');
export const C: Variable = variable('C');
export const D: Variable = variable('D');
export const E: Variable = variable('E');
export const F: Variable = variable('F');

export const S: Variable = variable('S');
export const T: Variable = variable('T');

export type EbnfGrammar = Grammar<ExtendedBackusNaurFormGrammarElement>;

export type BnfGrammar = Grammar<BackusNaurFormElement>;

export type BaseNormalizedGrammar = Grammar<BaseNormalizedFormGrammarElement>;

export const emptyGrammar = {startSymbol: A, rules: []};


