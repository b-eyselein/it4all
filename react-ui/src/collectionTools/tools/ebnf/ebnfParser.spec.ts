import {
  alternative,
  ExtendedBackusNaurFormGrammarElement,
  optional,
  repetitionAny,
  repetitionOne,
  sequence,
  terminal,
  UnaryOperator,
  Variable,
  variable
} from './ebnfElements';
import {Parser} from 'parsimmon';
import {ebnfGrammarLanguage, tryParseEbnfGrammarRight} from './ebnfParser';

const a = terminal('a');
const b = terminal('b');
const c = terminal('c');

const A: Variable = variable('A');
const B: Variable = variable('B');
const C: Variable = variable('C');

// Single elements

function testParseTerminal(parser: Parser<ExtendedBackusNaurFormGrammarElement>): void {
  test.each`
    toParse     | expected
    ${'\'0\''}    | ${terminal('0')}
    ${'\'1\''}    | ${terminal('1')}
    ${'\'a\''}    | ${a}
    ${'\'otto\''} | ${terminal('otto')}
    `(
    'should parse $toParse as terminal $expected',
    ({toParse, expected}) => expect(parser.tryParse(toParse)).toEqual(expected)
  );
}

describe('terminal', () => testParseTerminal(ebnfGrammarLanguage.terminal));

function testParseVariable(parser: Parser<ExtendedBackusNaurFormGrammarElement>): void {
  test.each`
    toParse | expected
    ${'A'}  | ${A}
    ${'B'}  | ${B}
    `(
    'should parse $toParse as variable $expected',
    ({toParse, expected}) => expect(parser.tryParse(toParse)).toEqual(expected)
  );
}

describe('variable', () => testParseVariable(ebnfGrammarLanguage.variable));

describe('singleElements', () => {
  testParseTerminal(ebnfGrammarLanguage.singleElement);
  testParseVariable(ebnfGrammarLanguage.singleElement);
});

// Unary elements

function nameForUnaryOperatorElement(op: UnaryOperator): string {
  if (op === '?') {
    return 'optional';
  } else if (op === '*') {
    return 'repetitionAny';
  } else /* if (op === '+') */ {
    return 'repetitionOne';
  }
}

function testParseUnary(parser: Parser<ExtendedBackusNaurFormGrammarElement>, operator: UnaryOperator, element: (child: ExtendedBackusNaurFormGrammarElement) => ExtendedBackusNaurFormGrammarElement): void {
  test.each`
    toParse          | child
    ${'A'}           | ${A}
    ${'\'a\''}         | ${a}
    ${'(A | B | C)'} | ${alternative(A, B, C)}
    `(
    `should parse $toParse${operator} as ${nameForUnaryOperatorElement(operator)} element with child element $child`,
    ({toParse, child}) => expect(parser.tryParse(`${toParse}${operator}`)).toEqual(element(child))
  );
}

describe('optional', () => testParseUnary(ebnfGrammarLanguage.unaryElement, '?', optional));

describe('repetitionAny', () => testParseUnary(ebnfGrammarLanguage.unaryElement, '*', repetitionAny));

describe('repetitionOne', () => testParseUnary(ebnfGrammarLanguage.unaryElement, '+', repetitionOne));

function testParseAlternative(parser: Parser<ExtendedBackusNaurFormGrammarElement>): void {
  test.each`
    toParse              | children
    ${'A | B'}           | ${[A, B]}
    ${'A | B | C'}       | ${[A, B, C]}
    ${'\'a\' | B | C'}   | ${[a, B, C]}
    ${'\'a\' | B? | C*'} | ${[a, optional(B), repetitionAny(C)]}
    ${'(A | B) | C'}     | ${[alternative(A, B), C]}
    ${'(A | B?) | C'}    | ${[alternative(A, optional(B)), C]}
    `(
    'should parse $toParse as alternative element with children $children',
    ({toParse, children}) => expect(parser.tryParse(toParse)).toEqual(alternative(...children))
  );
}

describe('alternative', () => testParseAlternative(ebnfGrammarLanguage.alternative));

function testParseSequence(parser: Parser<ExtendedBackusNaurFormGrammarElement>): void {
  test.each`
    toParse      | children
    ${'A B C'}   | ${[A, B, C]}
    ${'A | B C'} | ${[alternative(A, B), C]}
    `(
    'should parse $toParse as sequence with children $children',
    ({toParse, children}) => expect(parser.tryParse(toParse)).toEqual(sequence(...children))
  );
}

describe('sequence', () => testParseSequence(ebnfGrammarLanguage.sequence));


describe('ebnfGrammarElement', () => {
  test.each`
  toParse | awaited
  ${'A | B C B B'} | ${sequence(alternative(A, B), C, B, B)}
  `(
    'should parse $toParse as $awaited',
    ({toParse, awaited}) => expect(tryParseEbnfGrammarRight(toParse)).toEqual(awaited)
  );
});
