import {backusNaurForm2BaseNormalizedFormConverter} from './backusNaur2BaseNormalizedConverter';
import {A, B, BaseNormalizedGrammar, BnfGrammar, C, D, E, emptyGrammar, F, S, s, T, t, termNull, termOne} from './grammarTestHelpers';
import {alternative, BackusNaurFormElement, BaseNormalizedSequence, BasicGrammarElement, EmptyWord, sequence} from './ebnfElements';

type IE = BackusNaurFormElement;

const backusNaurGrammar1: BnfGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: B},
    {variable: B, right: C},
    {variable: C, right: D},
    {variable: D, right: alternative<IE>(A, sequence<IE>(s, A, t), EmptyWord)}
  ]
};

const baseNormalizedGrammar1: BaseNormalizedGrammar = {
  startSymbol: A,
  rules: [
    {variable: A, right: alternative(sequence(B))},
    {variable: B, right: alternative(sequence(C))},
    {variable: C, right: alternative(sequence(D))},
    {variable: D, right: alternative<BaseNormalizedSequence>(sequence(A), sequence<BasicGrammarElement>(s, A, t), sequence(EmptyWord))}
  ]
};

const backusNaurGrammar2: BnfGrammar = {
  startSymbol: T,
  rules: [
    {variable: T, right: S},
    {variable: S, right: sequence(alternative(A, B), alternative(C, D))},
    {variable: A, right: sequence(termNull, termOne, termNull)},
    {variable: B, right: termNull},
    {variable: C, right: termNull},
    {variable: D, right: termOne}
  ]
};

const baseNormalizedGrammar2: BaseNormalizedGrammar = {
  startSymbol: T,
  rules: [
    {variable: T, right: alternative(sequence(S))},
    {variable: S, right: alternative(sequence(E, F))},
    {variable: A, right: alternative(sequence(termNull, termOne, termNull))},
    {variable: B, right: alternative(sequence(termNull))},
    {variable: C, right: alternative(sequence(termNull))},
    {variable: D, right: alternative(sequence(termOne))},
    {variable: E, right: alternative(sequence(A), sequence(B))},
    {variable: F, right: alternative(sequence(C), sequence(D))},
  ]
};

describe('', () => {

  test.each`
  grammar               | awaited
  ${emptyGrammar}       | ${emptyGrammar}
  ${backusNaurGrammar1} | ${baseNormalizedGrammar1}
  ${backusNaurGrammar2} | ${baseNormalizedGrammar2}
  `(
    'should convert an backus naur form $grammar to base normalized form grammar $awaited',
    ({grammar, awaited}) => expect(backusNaurForm2BaseNormalizedFormConverter.convert(grammar)).toEqual(awaited)
  );

});
