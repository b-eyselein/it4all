import {parseBooleanFormula} from './boolean-formula-parser';
import {and, BooleanBinaryNode, BooleanFalse, BooleanOr, BooleanTrue, BooleanVariable, equiv, impl, nand, nor, not, or, xnor, xor} from './bool-node';

describe('BooleanFormulaParser', () => {
  const a: BooleanVariable = new BooleanVariable('a');
  const b: BooleanVariable = new BooleanVariable('b');
  const c: BooleanVariable = new BooleanVariable('c');

  const formula0 = 'a and b and c';
  const formula0Expected = and(a, and(b, c));

  const formula1 = '(a and b and not c) or (not a and b and not c)';
  const formula1Expected: BooleanOr = or(
    and(a, and(b, not(c))),
    and(not(a), and(b, not(c)))
  );

  const formula2 = '(a and b and not c) or (not a and b and not c) or (a and not b and c)';
  const formula2Expected: BooleanOr = or(and(a, and(b, not(c))), or(and(not(a), and(b, not(c))), and(a, and(not(b), c))));

  const formula3 = '(a equiv (b nand c)) xor (c impl (b xor b))';
  const formula3Expected: BooleanBinaryNode = xor(equiv(a, nand(b, c)), impl(c, xor(b, b)));

  const formula4 = '(a equiv b nand c) xor (c impl b nand c xor b)';
  const formula4Expected = xor(nand(equiv(a, b), c), xor(nand(impl(c, b), c), b));

  test.each`
  toParse    | expected
  ${'true'}  | ${BooleanTrue}
  ${'1'}     | ${BooleanTrue}
  ${'false'} | ${BooleanFalse}
  ${'0'}     | ${BooleanFalse}
  ${'a'}     | ${a}
  ${'b'}     | ${b}
  ${'not a'} | ${not(a)}
  ${'a and b'} | ${and(a, b)}
  ${'a or b'}  | ${or(a, b)}
  ${'a xor b'} | ${xor(a, b)}
  ${'a nor b'} | ${nor(a, b)}
  ${'a xnor b'} | ${xnor(a, b)}
  ${'a nand b'} | ${nand(a, b)}
  ${'a equiv b'} | ${equiv(a, b)}
  ${'a impl b'} | ${impl(a, b)}
  ${formula0} | ${formula0Expected}
  ${formula1} | ${formula1Expected}
  ${formula2} | ${formula2Expected}
  ${formula3} | ${formula3Expected}
  ${formula4} | ${formula4Expected}
  `(
    'it should parse $toParse as $expected',
    ({toParse, expected}) => expect(parseBooleanFormula(toParse)).toEqual(expected)
  );
});
