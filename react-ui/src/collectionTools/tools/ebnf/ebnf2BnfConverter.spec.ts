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
  variable
} from './ebnfElements';
import {A, B, BnfGrammar, C, D, E, EbnfGrammar, emptyGrammar, s, t, u} from './grammarTestHelpers';

type IE = ExtendedBackusNaurFormGrammarElement;
type OE = BackusNaurFormElement;

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
    {variable: A, right: alternative<OE>(EmptyWord, B)},
    {variable: B, right: alternative(<OE>EmptyWord, sequence(C, B))},
    {variable: C, right: alternative(<OE>s, sequence<OE>(s, C))},
  ]
};


const ebnfGrammar1: EbnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: sequence<OE>(B, s)},
    {variable: B, right: t}
  ]
};

const bnfGrammar1: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: sequence<OE>(B, s)},
    {variable: B, right: t}
  ]
};

const ebnfGrammar2: EbnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative<IE>(repetitionAny(B), sequence<IE>(repetitionAny(C), A), sequence<IE>(A, repetitionAny(B)))},
    {variable: B, right: sequence(t, u)},
    {variable: C, right: alternative(u, s)}
  ]
};

const bnfGrammar2: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative<OE>(D, sequence(E, A), sequence(A, D))},
    {variable: B, right: sequence(t, u)},
    {variable: C, right: alternative(u, s)},
    {variable: D, right: alternative<OE>(EmptyWord, sequence(variable('B'), variable('D')))},
    {variable: E, right: alternative<OE>(EmptyWord, sequence(variable('C'), variable('E')))}
  ]
};

describe('ebnf2BnfConverter', () => {
  test.each`
  grammar         | awaited
  ${emptyGrammar} | ${emptyGrammar}
  ${ebnfGrammar0} | ${bnfGrammar0}
  ${ebnfGrammar1} | ${bnfGrammar1}
  ${ebnfGrammar2} | ${bnfGrammar2}
  `(
    'it should convert an ebnf $grammar to an bnf $awaited',
    ({grammar, awaited}) => expect(ebnf2BnfConverter.convert(grammar)).toEqual(awaited)
  );

});
