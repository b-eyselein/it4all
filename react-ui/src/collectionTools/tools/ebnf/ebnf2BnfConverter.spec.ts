import {ebnf2BnfConverter} from './ebnf2BnfConverter';
import {
  alternative,
  BackusNaurFormElement,
  EmptyWord,
  ExtendedBackusNaurFormGrammarElement,
  optional,
  repetitionAny,
  repetitionOne,
  sequence,
  terminal,
  variable,
  Variable
} from './ebnfElements';
import {Grammar} from './grammar';

const s = terminal('s');
const t = terminal('t');
const u = terminal('u');

const A: Variable = variable('A');
const B: Variable = variable('B');
const C: Variable = variable('C');
const D: Variable = variable('D');
const E: Variable = variable('E');

type EbnfGrammar = Grammar<ExtendedBackusNaurFormGrammarElement>;
type BnfGrammar = Grammar<BackusNaurFormElement>;

const emptyEbnfGrammar = {startSymbol: A, rules: []};
const emptyBnfGrammar = {startSymbol: A, rules: []};

const ebnfGrammar0: EbnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: optional(B)},
    {variable: B, right: repetitionAny(C)},
    {variable: C, right: repetitionOne(s)}
  ]
};

const bnfGrammar0: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative(EmptyWord, B)},
    {variable: B, right: alternative(EmptyWord, sequence(C, B))},
    {variable: C, right: alternative(s, sequence(s, C))},
  ]
};


const ebnfGrammar1: EbnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: sequence(B, s)},
    {variable: B, right: t}
  ]
};

const bnfGrammar1: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: sequence(B, s)},
    {variable: B, right: t}
  ]
};

const ebnfGrammar2: EbnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative(repetitionAny(B), sequence(repetitionAny(C), A), sequence(A, repetitionAny(B)))},
    {variable: B, right: sequence(t, u)},
    {variable: C, right: alternative(u, s)}
  ]
};

const bnfGrammar2: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative(D, sequence(E, A), sequence(A, D))},
    {variable: B, right: sequence(t, u)},
    {variable: C, right: alternative(u, s)},
    {variable: D, right: alternative(EmptyWord, sequence(variable('B'), variable('D')))},
    {variable: E, right: alternative(EmptyWord, sequence(variable('C'), variable('E')))}
  ]
};

describe('ebnf2BnfConverter', () => {

  // ${ebnfGrammar2}     | ${bnfGrammar2}

  test.each`
  grammar             | awaited
  ${emptyEbnfGrammar} | ${emptyBnfGrammar}
  ${ebnfGrammar0}     | ${bnfGrammar0}
  ${ebnfGrammar1}     | ${bnfGrammar1}
  `(
    'it should convert an ebnf $grammar to an bnf $awaited',
    ({grammar, awaited}) => expect(ebnf2BnfConverter.convert(grammar)).toEqual(awaited)
  );

  it('should...', () => {
    expect(ebnf2BnfConverter.convert(ebnfGrammar2)).toEqual(bnfGrammar2);
  });
});
